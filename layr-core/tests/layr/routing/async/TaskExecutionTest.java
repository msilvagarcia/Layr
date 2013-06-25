package layr.routing.async;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;
import layr.api.Cache;
import layr.commons.Listener;
import layr.engine.TemplateParser;
import layr.engine.components.template.TemplateComponentFactory;
import layr.engine.components.xhtml.XHtmlComponentFactory;
import layr.exceptions.NotFoundException;
import layr.exceptions.RoutingException;
import layr.exceptions.RoutingInitializationException;
import layr.routing.impl.StubApplicationContext;
import layr.routing.impl.StubRequestContext;
import layr.routing.impl.StubRoutingBootstrap;
import layr.routing.lifecycle.BusinessRoutingLifeCycle;

import org.junit.Before;
import org.junit.Test;

public class TaskExecutionTest {

	static final String THEME_XHTML = "stress-template-theme.xhtml";
	static final int MANY_TIMES = 25000;
	static final int NUM_THREADS = MANY_TIMES;
	static final Cache CACHE = new Cache();

	static final ExecutorService CPUEXECUTOR = Executors.newFixedThreadPool(4+1);
	static final ExecutorService LONGIOEXECUTOR = Executors.newCachedThreadPool();
	
	StubApplicationContext applicationContext;
	CountDownLatch countDownLatch;
	AtomicInteger failureCount;
	
	@Before
	public void setup() throws RoutingInitializationException{
		StubRoutingBootstrap bootstrap = new StubRoutingBootstrap();
		applicationContext = (StubApplicationContext) bootstrap.configure(classes(
			StressTemplateResource.class,
			TemplateComponentFactory.class,
			XHtmlComponentFactory.class));
		applicationContext.setMethodExecutionThreadPool(CPUEXECUTOR);
		applicationContext.getRegisteredTagLibs().put("", new XHtmlComponentFactory());
		countDownLatch = new CountDownLatch(MANY_TIMES);
		failureCount = new AtomicInteger(0);
	}

	Set<Class<?>> classes(Class<?>...classes) {
		HashSet<Class<?>> set = new HashSet<Class<?>>();
		for ( Class<?>clazz: classes )
			set.add(clazz);
		return set;
	}

	@Test//(timeout=2500)
	public void grantThatCouldRenderHelloManyTimesInCachedMode() throws Exception {
		for (int i = 0; i < MANY_TIMES; i++)
			simulateRequest(i);
		countDownLatch.await();
		Assert.assertEquals(0, failureCount.get());
	}

	void precompileTemplateSharingSameCache() throws Exception {
		TemplateParser parser = new TemplateParser(createCachedContext(0));
		parser.compile(THEME_XHTML).render();
	}
	
	void simulateRequest(int i) throws RoutingException, NotFoundException{
		StubRequestContext context = createCachedContext(i);
		context.setRequestURI("/stress/");
		context.setRequestHttpMethod("GET");
		BusinessRoutingLifeCycle lifeCycle = createLifeCycle(context);
		lifeCycle.canHandleRequest();
		lifeCycle.run();
	}

	private BusinessRoutingLifeCycle createLifeCycle(StubRequestContext context) {
		BusinessRoutingLifeCycle lifeCycle = new BusinessRoutingLifeCycle(applicationContext, context);
		lifeCycle.onSuccess(createOnSuccessListener());
		lifeCycle.onFail(createOnFailListener());
		return lifeCycle;
	}

	StubRequestContext createContext(int i) {
		StubRequestContext context = new StubRequestContext();
		context.put("formNumber", i);
		context.setIsAsyncRequest(true);
		context.setRegisteredTagLibs(applicationContext.getRegisteredTagLibs());
		return context;
	}

	StubRequestContext createCachedContext(int i) {
		StubRequestContext context = createContext(i);
		context.setCache(CACHE);
		return context;
	}

	public Listener<Object> createOnSuccessListener(){
		return new Listener<Object>() {
			public void listen(Object result) {
				countDownLatch.countDown();
			}
		};
	}

	public Listener<Exception> createOnFailListener(){
		return new Listener<Exception>() {
			public void listen(Exception result) {
				countDownLatch.countDown();
				failureCount.incrementAndGet();
			}
		};
	}
}

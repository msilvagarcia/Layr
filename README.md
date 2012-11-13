# Layr - The Fast Web Prototyping Framework

## O que é?

O Layr é um framework desenvolvido com o intuito de se desenvolver aplicações Web tendo em mente
Free Logic Markup, mas sem romper com os padrões da Web. Ele permite rápido reaproveitamento de
código HTML, provendo componentização e modularização do Front-End através do já conhecido XHTML 1/1.1/5.

## Por que Free-Logic Markup ?

 - qdo trabalhamos com logica no front-end ( *SP - Server Pages )
   - manutenção das lógicas de interface é mais dificultosa ->
      - impede a reutilização de lógicas que se repetem em visões (telas) diferentes
      - dificulta os testes unitários das regras de interface - obriga a fazer teste de integração para testar
        lógicas simples
 - o processo de diagramação deveria ser simples a ponto de qualquer designer com noções de HTML conseguir dar
   manutenção. Se usarmos ferramentas de template baseadas em JavaScript ( Dusty.js, JQuery Templates, etc..),
   ou misturarmos lógica no cógido, iremos dificultar o processo de diagramação de um designer.

## Por que XHTML?

 - todos os navegadores já interpretam bem este modelo de marcação ( afinal já é um padrão amplamente adotado )
 - inclusive, na prática, o que os navegadores exibem sempre é HTML, se fossemos usar um outro de tipo marcação para
   gerar o HTML, iriamos trazer mais um ponto de aprendizado na hora de dar manutenção no código fonte.
 - não requer o conhecimento de nada além daquilo que o navegador oferece por padrão

## Componentização de modo simples e fácil

 - graças aos namespaces temos a capacidade de criar tags customizadas sem ferir o HTML padrão. O formulário
   abaixo foi escrito usando a taxonomia do tema Bootstrap ( do Twitter ).

```xhtml
<div xmlns="http://www.w3.org/1999/xhtml">

  <form class="form-horizontal" action="/user/save" method="POST">
    <div class="control-group">
      <label class="control-label">Email</label>
      <div class="controls">
        <input type="text" name="inputEmail" placeholder="Email">
      </div>
    </div>
    <div class="control-group">
      <label class="control-label">Password</label>
      <div class="controls">
        <input type="password" name="inputPassword" placeholder="Password">
      </div>
    </div>
    <div class="control-group">
      <div class="controls">
        <label class="checkbox">
          <input type="checkbox"> Remember me
        </label>
        <button type="submit" class="btn">Sign in</button>
      </div>
    </div>
  </form>

</div>
```

 - Note que as tags que representam um item do formulário está se repetindo com frequencia. Memorizar
   as classes CSS e sua estrutura nem sempre é fácil. Mas, e se pudessemos usar uma sintaxe mais amigável
   que nos permita re-utilizar estas estruturas sem repetição de código?

```xhtml
<div xmlns="http://www.w3.org/1999/xhtml"
     xmlns:ui="urn:components">

  <ui:HorizontalForm action="/user/save" method="POST">

    <ui:FormItem label="Email:">
      <ui:TextField name="inputEmail" placeholder="Email" />
    </ui:FormItem>

    <ui:FormItem label="Password:">
      <ui:PasswordField name="inputPassword" placeholder="Password" />
    </ui:FormItem>

    <ui:FormItem>
      <ui:Checkbox label="Remember me" name="rememberme" />
      <ui:SubmitButton label="Sign in" />
    </ui:FormItem>

  </ui:HorizontalForm>

</div>
```

 - Você deve ter notado que a quantidade de código escrita diminuiu um pouco. Para isso, foram criados
   vários arquivos .xhtml com os seus antigos conteúdos. O nome da tag deve ser o nome do arquivo. A
   localização destes arquivos foram definidos através do namespace **ui**, especificada na tag raíz do
   documento. Através da notação urn, definifimos que eles ficarão na pasta **components** ( raíz do
   class-path da aplicação* ). Veja como ficou o corpo do componente FormItem:

```xhtml
<div xmlns="http://www.w3.org/1999/xhtml"
   xmlns:tpl="urn:layr:template"
		class="FormItem control-group">

	<label class="pull-left">#{FormItem:label}</label>
	<tpl:children />

</div>
```

 - O componente **children** define o local aonde os filhos do componente serão renderizados. Esta
   tag foi desenvolvida através do conjunto de tags ( TagLibs ) de template ( definido pelo namespace
   **tpl** ). Esta TagLib acompanha o Layr e foi desenhada para auxliar no desenvolvimento de suas
   telas. Você pode usar esta mesma abordagem para traduzir qualquer conjunto de tags em componentes,
   não há limitação de tamanho ou quantidade de componente.

## Navegação Natural

 - Vamos imaginar que sua aplicação roda no URL http://localhost:8080/, e que sua estrutura de projeto
   prevê que a raíz do aplicativo está numa pasta chamada **source**. Abaixo um exemplo de como poderia
   estar o seu projeto usando o Layr:

<pre>
  |-- source
  |   |-- user
  |   |   |-- edit.xhtml
  |   |   |-- view.xhtml
  |   |   |-- add.xhtml
  |   |   |-- list.xhtml
  |   |-- theme
  |   |   |-- theme.xhtml
  |   |   |-- theme.css
  |   |-- components
  |   |   |-- FormItem.xhtml
  |   |   |-- Checkbox.xhtml
  |   |   |-- TextField.xhtml
  |   |   |-- PasswordField.xhtml
</pre>

 - Durante todo o desenvolvimento de um projeto, investe-se tempo organizando e definindo boas taxonomias
   para seus arquivos. Com o tempo, isto se torna um hábito natural do desenvolvedor. Se pudessemos aproveitar
   este esforço para definir, por exmplo, a maneira com que os _resources_ serão acessados pelo navegador,
   ou seja, como será definida a URL em que o usuário irá navegar pelo sistema, então ganhariamos tempo de
   desenvolvimento. A isso, é dado o nome de Navegação Natural.

 - Sendo assim, imagine que tu precisas exibir a tela de listagem de usuários. Você poderia acessá-lo através da URL
   http://localhost:8080/user/list/. Note que aqui estamos falando apenas de apresentação, em momento algum estamos
   nos preocupando em como vamos obter os dados para serem exibidos, ou como os salvaremos no banco.

## Navegação Condicionada ao Negócio

 - Por vezes, direcionamos os nossos usuários de nossos softwares à páginas baseado em tomadas de decisão de negócio.
   Imagine que um cliente de seu software foi selecionado para testar uma tela nova do sistema. Quando ele selecionar
   o URL http://localhost:8080/product/list/, ele deveria ver a versão nova da tela de listagem de produtos, enquanto
   os usuários antigos deveriam continuar vendo a página antiga. Abaixo um exemplo de como isso pode ser feito usando
   o plugin de Business Routing do Layr:

```java
@WebResource("/product/")
public class HomeResource {

  Products products;
  UserSession userSession; // A sample code that represents the current logged in user

  String productListTemplateName;

	@Route(
		pattern="/list/",
		template="/products/#{productListTemplateName}.xhtml")
	public void listProducts(){
		if ( userSession.isBetaUser() )
			productListTemplateName = "listBeta";
		else
			productListTemplateName = "list";
	}

}
```

```java
@WebResource("/user/")
public class UserResource {

  String redirectTo = "";
	
	@Route(
		pattern="/#{id}/edit",
		template="user/editForm.xhtml",
		redirectTo="#{redirectTo}" )
	public void editUser(
		@Parameter("id") Long userId ){
		if ( haveUserBillingPendencies() ){
			redirectTo = "/user/warning/";
			return;
		}
		
		// load user information with userId.
	}

	public boolean haveUserBillingPendencies(){
		return new Date().getTime() % 2 == 0;
	}

}
```

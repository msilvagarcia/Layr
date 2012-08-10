package layr.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import layr.util.StringUtil;


import com.google.gson.Gson;

/**
 * @author Miere Teixeira
 *
 * @param <E>
 */
@SuppressWarnings("serial")
public abstract class JsonUploadServlet<E> extends HttpServlet {
	public static final String DEFAULT_UPLOAD_DIR = "/tmp/upload";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doUpload(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doUpload(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doUpload(req, resp);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doUpload(req, resp);
	}

	public void doUpload(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		Collection<Part> parts = request.getParts();
		ArrayList<E> createdObjects = new ArrayList<E>();
		
		for ( Part part : parts) {
			String fileName = getFileName(part);
			writeFile(part, fileName);

			try {
				createdObjects.add(saveFile(fileName, part));
			} catch (Exception e) {
				throw new ServletException("Can't save the file.", e);
			}
		}

		PrintWriter writer = response.getWriter();
		writer.write( new Gson().toJson(createdObjects) );
	}

	/**
	 * @param part
	 * @return
	 */
	public String getFileName(Part part) {
		return "uploadedfile"+new Date().getTime();
	}

	/**
	 * @param fileName
	 * @param part
	 * @return
	 * @throws Exception
	 */
	public abstract E saveFile(String fileName, Part part) throws Exception;

	/**
	 * @param part
	 * @param fileName
	 * @throws IOException
	 */
	public void writeFile(Part part, String fileName) throws IOException {
		String uploadDir = getUploadDir();
		part.write(uploadDir+"/"+fileName);
	}

	public static String getUploadDir(){
		String uploadDir = System.getProperty("layr.upload.dir");
		if (StringUtil.isEmpty(uploadDir))
			uploadDir = DEFAULT_UPLOAD_DIR;
		return uploadDir;
	}

}

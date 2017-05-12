package cn.image_web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;

import java.awt.FileDialog;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;

import cn.image.process.ImageEnhance;
import cn.image.process.ImageProcessProgram;
import cn.image.util.GetPix;

import javax.imageio.ImageIO;
import javax.swing.*;



public class UploadServlet extends HttpServlet {  
   private boolean isMultipart;
   private String filePath;
   private String imgOutPath;
   private String fileName;
   private int maxFileSize = 150 * 1024*1024;
   private int maxMemSize = 1* 1024;
   private File file ;
   private ImageProcessProgram imgProcess;
   private Image img,oimg,lapimg,lapcvimg,sobimg,sobcvimg;
   private int iw,ih,ow,oh;
   int[] pixels,pixback; 
   
   ImageEnhance enhance;         
   GetPix common;
   
   public void init( ){
	  
      // Get the file location where it would be stored.
      filePath =getServletContext().getInitParameter("file-upload"); 
   }
  
   public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, java.io.IOException {
      // 检查有上传文件的请求
      System.out.println("点击了上传");
	   isMultipart = ServletFileUpload.isMultipartContent(request);
      
      response.setContentType("text/html");//设置内容模式为html
      
      java.io.PrintWriter out = response.getWriter( );
      
      if( !isMultipart ){
         out.println("<html>");
         out.println("<head>");
         out.println("<title>Servlet upload</title>");  
         out.println("</head>");
         out.println("<body>");
         out.println("<p>No file uploaded</p>"); 
         out.println("</body>");
         out.println("</html>");
         return;
      }
      DiskFileItemFactory factory = new DiskFileItemFactory();
      
   // 设置最多只允许在内存中存储的数据
      factory.setSizeThreshold(maxMemSize);
      // Location to save data that is larger than maxMemSize.
      factory.setRepository(new File("c:\\data"));
      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);
      // maximum file size to be uploaded.
      upload.setSizeMax( maxFileSize );
      try{ 
      // Parse the request to get file items.
      List fileItems = upload.parseRequest(request);
      // Process the uploaded file items
      Iterator i = fileItems.iterator();
      out.println("<html>");
      out.println("<head>");
      out.println("<title>Servlet upload</title>");  
      out.println("</head>");
      out.println("<body>");
      while ( i.hasNext () ) 
      {
         FileItem fi = (FileItem)i.next();
         if ( !fi.isFormField () )  
         {
            // Get the uploaded file parameters
            String fieldName = fi.getFieldName();
            System.out.println("fieldName="+fieldName);
            fileName = fi.getName();
            System.out.println("fileName="+fileName);
            String contentType = fi.getContentType();
            System.out.println("contentType="+contentType);
            boolean isInMemory = fi.isInMemory();
            long sizeInBytes = fi.getSize();
            // Write the file
            System.out.println(fileName.lastIndexOf("\\"));
            if( fileName.lastIndexOf("\\") >= 0 ){
               file = new File( filePath + 
               fileName.substring( fileName.lastIndexOf("\\"))) ;
               System.out.println(filePath);
               System.out.println(fileName.substring( fileName.lastIndexOf("\\")));
               System.out.println("路径"+filePath +fileName.substring( fileName.lastIndexOf("\\")));
            }else{
            	imgOutPath=filePath + fileName.substring(fileName.lastIndexOf("\\")+1);
               file = new File(imgOutPath) ;
               System.out.println(filePath + fileName.substring(fileName.lastIndexOf("\\")+1));
               
            }
            fi.write( file ) ;
            out.println("Uploaded Filename: " + fileName + "<br>");
         }
      }
      out.println("</body>");
      out.println("</html>");
      
      File file=new File(imgOutPath);
      InputStream is=new FileInputStream(file);
      BufferedImage bi=ImageIO.read(is);
      img=(Image)bi;
   
	  imgDeal(1,50);//laplace算子
	  
	  imgDeal(2,50);//laplace反转
	  
	  imgDeal(3,150);//sobel算子
	    
	  imgDeal(4,150);//sobel反转
	  
	  imgDeal(5,0);//图像锐化
	    
	  imgDeal(6,0);//图像模糊
	  
    
   }catch(Exception ex) {
       System.out.println(ex);
   }
   }
private void  imgDeal(int type,int t) throws IOException{
	 
	  imgProcess = new   ImageProcessProgram(); 
      iw = img.getWidth(null);
	  ih = img.getHeight(null);  
	  enhance = new ImageEnhance();    
      common  = new GetPix(); 
	  pixels = common.grabber(img, iw, ih);
	 // pixels=enhance.detect(pixels, iw, ih, type, t);
	  pixels=enhance.detect(pixels, iw, ih, type,t);
	  ImageProducer ipo = new MemoryImageSource(iw, ih, pixels, 0, iw);
	  oimg= imgProcess.createImage(ipo);
	  saveImg(oimg,type); 
	 
}
private void saveImg(Image oimg,int type) throws IOException {
	//首先创建一个BufferedImage变量，因为ImageIO写图片用到了BufferedImage变量。 
	      BufferedImage obi = new BufferedImage(iw, ih, BufferedImage.TYPE_3BYTE_BGR);
	//再创建一个Graphics变量，用来画出来要保持的图片，及上面传递过来的Image变量 
	      Graphics g = obi.getGraphics(); 
	      g.drawImage(oimg, 0, 0, null);
	//将BufferedImage变量写入文件中。 
	      if(type==1){
	    	  ImageIO.write(obi,"jpg",new File("C:/Program Files/Apache Software Foundation/Tomcat 8.0/webapps/imageprocess/odata/lap"+fileName));  		  
	      }else if(type==2)
	      {
	    	  ImageIO.write(obi,"jpg",new File("C:/Program Files/Apache Software Foundation/Tomcat 8.0/webapps/imageprocess/odata/lapcv"+fileName));
	      }else if(type==3)
	      {
	    	  ImageIO.write(obi,"jpg",new File("C:/Program Files/Apache Software Foundation/Tomcat 8.0/webapps/imageprocess/odata/sob"+fileName));
	      }else if(type==4)
	      {
	    	  ImageIO.write(obi,"jpg",new File("C:/Program Files/Apache Software Foundation/Tomcat 8.0/webapps/imageprocess/odata/sobcv"+fileName));
	      }else if(type==5)
	      {
	    	  ImageIO.write(obi,"jpg",new File("C:/Program Files/Apache Software Foundation/Tomcat 8.0/webapps/imageprocess/odata/sharp"+fileName));
	      }else 
	      {
	    	  ImageIO.write(obi,"jpg",new File("C:/Program Files/Apache Software Foundation/Tomcat 8.0/webapps/imageprocess/odata/blurry"+fileName));
	      }
	     
}
   public void doGet(HttpServletRequest request, 
                       HttpServletResponse response)
        throws ServletException, java.io.IOException {      
        throw new ServletException("GET method used with " +
                getClass( ).getName( )+": POST method required.");
   } 
}
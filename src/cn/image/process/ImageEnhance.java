/**
 * @ImageEnhance.java
 * @Version 1.0 2009.02.17
 * @Author Xie-Hua Sun * 
 */

package cn.image.process;

import java.awt.image.ColorModel;

public class ImageEnhance
{

	//锐化--------------------------------------------------
	
	
    public int[] detect(int[] px, int iw, int ih, int num, 
                        int thresh)
    {
		int i, j, r, g, b;
	    int[][] inr   = new int[iw][ih];//红色分量矩阵
	    int[][] ing   = new int[iw][ih];//绿色分量矩阵
	    int[][] inb   = new int[iw][ih];//蓝色分量矩阵
	    int[][] gray  = new int[iw][ih];//灰度图像矩阵
	    
	    ColorModel cm = ColorModel.getRGBdefault();
	    
	    for(j = 0; j < ih; j++)
	    {	    
	        for(i = 0; i < iw; i++)
	        {
	        	inr[i][j] = cm.getRed(px[i+j*iw]);
	            ing[i][j] = cm.getGreen(px[i+j*iw]);
	            inb[i][j] = cm.getBlue(px[i+j*iw]);
	            
	            //转变为灰度图像矩阵
	            gray[i][j] = (int)((inr[i][j]+ing[i][j]+inb[i][j])/3.0);
	        }
	    }	           
	    
	    if(num == 1)                       //Laplace
	    {
		    byte[][] lap1 = {{ 1, 1, 1},
	                         { 1,-8, 1},
	                         { 1, 1, 1}};
		    byte[][] lap2 = { { -1, -1, -1},
                    	      { -1,8,-1},
                    	      { -1, -1, -1}};
		 
	        	int[][] edge = edge(gray, lap1, iw, ih);
			    
				for(j = 0; j < ih; j++) //高度像素
			    {
			        for(i = 0; i < iw; i++)  //宽度像素
			        {			        	
			        	if(edge[i][j] > thresh) r = 255;//黑色，边缘点
			        	else r = 0;
			        	px[i+j*iw] = (255<<24)|(r<<16)|(r<<8)|r;
			        	
			        }
			    }
	         
	    }else if(num == 2)                       //Laplace
	    {
		    byte[][] lap1 = {{ 1, 1, 1},
	                         { 1,-8, 1},
	                         { 1, 1, 1}};
	
	      
	        	int[][] edge = edge(gray, lap1, iw, ih);
			    
				for(j = 0; j < ih; j++) //高度像素
			    {
			        for(i = 0; i < iw; i++)  //宽度像素
			        {			        	
			        	if(edge[i][j] > thresh) r = 0;//黑色，边缘点
			        	else r = 255;
			        	px[i+j*iw] = (255<<24)|(r<<16)|(r<<8)|r;
			        	
			        }
			    }
	        
	      
	    } else if(num == 3)//Sobel
	    {
	    	byte[][] sob1 = {{ 1, 0,-1},
	                         { 2, 0,-2},
	                         { 1, 0,-1}};
		    
		    byte[][] sob2 = {{ 1, 2, 1},
	                         { 0, 0, 0},
	                         {-1,-2,-1}};	
		  
		   
	        	int[][] edge1 = edge(gray, sob1, iw, ih);
			    int[][] edge2 = edge(gray, sob2, iw, ih);
				for(j = 0; j < ih; j++)
			    {
			        for(i = 0; i < iw; i++)
			        {
			        	//if(Math.max(edge1[i][j],edge2[i][j]) > thresh) r = 0;
			        	if((edge1[i][j]+edge2[i][j]) > thresh) r = 255;
			        	else r = 0;
			        	px[i+j*iw] = (255<<24)|(r<<16)|(r<<8)|r;			        	
			        }
			    }
	        
	       
	    }else if(num == 4)//Sobel 反
	    {
	    	byte[][] sob1 = {{ 1, 0,-1},
	                         { 2, 0,-2},
	                         { 1, 0,-1}};
		    
		    byte[][] sob2 = {{ 1, 2, 1},
	                         { 0, 0, 0},
	                         {-1,-2,-1}};	
		    
		  
	        	int[][] edge1 = edge(gray, sob1, iw, ih);
			    int[][] edge2 = edge(gray, sob2, iw, ih);
				for(j = 0; j < ih; j++)
			    {
			        for(i = 0; i < iw; i++)
			        {
			        	if((edge1[i][j]+edge2[i][j]) > thresh) r = 0;
			        	else r = 255;
			        	px[i+j*iw] = (255<<24)|(r<<16)|(r<<8)|r;
			        	
			        }
			     
			    
	        }
	       
	    }else if(num == 5)                       //锐化
	    {
		  
		    byte[][] lap1 = { { 0, -1, 0},
		    				  { -1,5,-1},
		    				  { 0, -1,0}};
		                            
			    int[][] edger = edge(inr, lap1, iw, ih);
			    int[][] edgeg = edge(ing, lap1, iw, ih);
			    int[][] edgeb = edge(inb, lap1, iw, ih);
				
				for(j = 0; j < ih; j++)
			    {
			        for(i = 0; i < iw; i++)
			        {
			           	r = edger[i][j];
			        	g = edgeg[i][j];
			        	b = edgeb[i][j];
			        	px[i+j*iw] = (255<<24)|(r<<16)|(g<<8)|b;			        	
			        }
			    }
		    }else if(num == 6) {		 //模糊
		    double[][] lap1 = { { 0, 0.252, 0},
		    				  { 0.252,0,0.252},
		    				  { 0, 0.252,0}};
		                            
			    int[][] edger = dEdge(inr, lap1, iw, ih);
			    int[][] edgeg = dEdge(ing, lap1, iw, ih);
			    int[][] edgeb =dEdge(inb, lap1, iw, ih);
				
				for(j = 0; j < ih; j++)
			    {
			        for(i = 0; i < iw; i++)
			        {
			           	r = edger[i][j];
			        	g = edgeg[i][j];
			        	b = edgeb[i][j];
			        	px[i+j*iw] = (255<<24)|(r<<16)|(g<<8)|b;			        	
			        }
			    }
		    }	    
	    return px;
	}
	
	public int[][] edge(int[][] in, byte[][] tmp, int iw, int ih) 
	{
		int[][] ed = new int[iw][ih];
		
		for(int j = 1; j < ih-1; j++)
		{		
			for(int i = 1; i < iw-1; i++)
			{
				ed[i][j] = Math.abs(tmp[0][0]*in[i-1][j-1]+tmp[0][1]*in[i-1][j]+
				                    tmp[0][2]*in[i-1][j+1]+tmp[1][0]*in[i][j-1]+
			                        tmp[1][1]*in[i][j]    +tmp[1][2]*in[i][j+1]+
				                    tmp[2][0]*in[i+1][j-1]+tmp[2][1]*in[i+1][j]+
				                    tmp[2][2]*in[i+1][j+1]);	            						
			}
		}
		return ed;
	}
	public int[][] dEdge(int[][] in, double[][] tmp, int iw, int ih) 
	{
		int[][] ed = new int[iw][ih];
		
		for(int j = 1; j < ih-1; j++)
		{		
			for(int i = 1; i < iw-1; i++)
			{
				ed[i][j] = (int) Math.abs(tmp[0][0]*in[i-1][j-1]+tmp[0][1]*in[i-1][j]+
				                    tmp[0][2]*in[i-1][j+1]+tmp[1][0]*in[i][j-1]+
			                        tmp[1][1]*in[i][j]    +tmp[1][2]*in[i][j+1]+
				                    tmp[2][0]*in[i+1][j-1]+tmp[2][1]*in[i+1][j]+
				                    tmp[2][2]*in[i+1][j+1]);	
				
			}
		}
		return ed;
	}
	
	
	
	
}
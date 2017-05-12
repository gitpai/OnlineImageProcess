 /**
 * @Common.java
 * @Version 1.0 2010.03.05
 * @Author Xie-Hua Sun 
 */
 
package cn.image.util;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import com.sun.image.codec.jpeg.*;

public class GetPix extends Frame
{
	//关于图像文件和像素--------------------------    
    public int[] grabber(Image im, int iw, int ih)
	{
		int [] pix = new int[iw * ih];
		try
		{
		    PixelGrabber pg = new PixelGrabber(im, 0, 0, iw,  ih, pix, 0, iw);
		    pg.grabPixels();
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}	
		return pix;
	}
	


}

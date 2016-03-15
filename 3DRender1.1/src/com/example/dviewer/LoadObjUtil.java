package com.example.dviewer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.content.res.Resources;
import android.util.Log;

public class LoadObjUtil 
{
	//�����������Ĳ��
	public static float[] getCrossProduct(float x1,float y1,float z1,float x2,float y2,float z2)
	{		
		//�������ʸ�����ʸ����XYZ��ķ���ABC
        float A=y1*z2-y2*z1;
        float B=z1*x2-z2*x1;
        float C=x1*y2-x2*y1;
		
		return new float[]{A,B,C};
	}
	
	//�������
	public static float[] vectorNormal(float[] vector)
	{
		//��������ģ
		float module=(float)Math.sqrt(vector[0]*vector[0]+vector[1]*vector[1]+vector[2]*vector[2]);
		return new float[]{vector[0]/module,vector[1]/module,vector[2]/module};
	}
	
	static boolean stFlag=false;
	public static boolean getSTFlag()
	{
		return stFlag;
	}
	public static void setSTFlag(boolean tf)
	{
		 stFlag=tf;
	}
	//��obj�ļ��м���Я��������Ϣ�����壬���Զ�����ÿ�������ƽ��������MySurfaceView mv
    public static Model loadFromFile(String fname, Resources r)
    {
    	//���غ����������
    	Model model=null;
    	//ԭʼ���������б�--ֱ�Ӵ�obj�ļ��м���
    	ArrayList<Float> alv=new ArrayList<Float>();
    	//������װ�������б�--���������Ϣ���ļ��м���
    	ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();
    	//������������б�--������֯��
    	ArrayList<Float> alvResult=new ArrayList<Float>();    	
    	//ƽ��ǰ����������Ӧ�ĵ�ķ���������Map
    	//��HashMap��keyΪ��������� valueΪ�����ڵĸ�����ķ������ļ���
    	HashMap<Integer,HashSet<NormalUtil>> hmn=new HashMap<Integer,HashSet<NormalUtil>>();  
    	//ԭʼ���������б�
    	ArrayList<Float> alt=new ArrayList<Float>();  
    	//�����������б�
    	ArrayList<Float> altResult=new ArrayList<Float>();  
    	try
    	{
    		InputStream in=r.getAssets().open(fname);
    		InputStreamReader isr=new InputStreamReader(in);
    		BufferedReader br=new BufferedReader(isr);
    		String temps=null;
    		
    		//ɨ���ļ������������͵Ĳ�ִͬ�в�ͬ�Ĵ����߼�
		    while((temps=br.readLine())!=null) 
		    {
		    	//�ÿո�ָ����еĸ�����ɲ���
		    	String[] tempsa=temps.split("[ ]+");
		      	if(tempsa[0].trim().equals("v"))
		      	{//����Ϊ��������
		      	    //��Ϊ��������������ȡ���˶����XYZ�������ӵ�ԭʼ���������б���
		      		alv.add(Float.parseFloat(tempsa[1]));
		      		alv.add(Float.parseFloat(tempsa[2]));
		      		alv.add(Float.parseFloat(tempsa[3]));
		      	}
		    	else if(tempsa[0].trim().equals("vt"))
		      	{//����Ϊ����������
		      		//��Ϊ��������������ȡST���겢���ӽ�ԭʼ���������б���
		      		alt.add(Float.parseFloat(tempsa[1])/2f);
		      		alt.add(Float.parseFloat(tempsa[2])/2f); 
		      	}
		      	else if(tempsa[0].trim().equals("f"))
		      	{//����Ϊ��������
		      		/*
		      		 *��Ϊ��������������� �����Ķ����������ԭʼ���������б���
		      		 *��ȡ��Ӧ�Ķ�������ֵ���ӵ�������������б��У�ͬʱ��������
		      		 *�����������������ķ����������ӵ�ƽ��ǰ����������Ӧ�ĵ�
		      		 *�ķ�����������ɵ�Map��
		      		*/
		      		//�ļ���ʽΪ��f: v����f: v//vn
		      		if(tempsa[1].matches("[0-9]+")||tempsa[1].matches("[0-9]+//[0-9]+"))
		      		{
		      			int[] index=new int[3];//������������ֵ������
			      		
			      		//�����0�����������������ȡ�˶����XYZ��������	      		
			      		index[0]=Integer.parseInt(tempsa[1].split("/")[0])-1;
			      		float x0=alv.get(3*index[0]);
			      		float y0=alv.get(3*index[0]+1);
			      		float z0=alv.get(3*index[0]+2);
			      		alvResult.add(x0);
			      		alvResult.add(y0);
			      		alvResult.add(z0);		
			      		
			      	    //�����1�����������������ȡ�˶����XYZ��������	  
			      		index[1]=Integer.parseInt(tempsa[2].split("/")[0])-1;
			      		float x1=alv.get(3*index[1]);
			      		float y1=alv.get(3*index[1]+1);
			      		float z1=alv.get(3*index[1]+2);
			      		alvResult.add(x1);
			      		alvResult.add(y1);
			      		alvResult.add(z1);
			      		
			      	    //�����2�����������������ȡ�˶����XYZ��������	
			      		index[2]=Integer.parseInt(tempsa[3].split("/")[0])-1;
			      		float x2=alv.get(3*index[2]);
			      		float y2=alv.get(3*index[2]+1);
			      		float z2=alv.get(3*index[2]+2);
			      		alvResult.add(x2);
			      		alvResult.add(y2); 
			      		alvResult.add(z2);	
			      		
			      		//��¼����Ķ�������
			      		alFaceIndex.add(index[0]);
			      		alFaceIndex.add(index[1]);
			      		alFaceIndex.add(index[2]);
			      		
			      		//ͨ��������������������0-1��0-2�����õ�����ķ�����
			      	    //��0�ŵ㵽1�ŵ������
			      		float vxa=x1-x0;
			      		float vya=y1-y0;
			      		float vza=z1-z0;
			      	    //��0�ŵ㵽2�ŵ������
			      		float vxb=x2-x0;
			      		float vyb=y2-y0;
			      		float vzb=z2-z0;
			      	    //ͨ�������������Ĳ�����㷨����
			      		float[] vNormal=vectorNormal(getCrossProduct
						      			(
						      					vxa,vya,vza,vxb,vyb,vzb
						      			));
			      	    
			      		for(int tempInxex:index)
			      		{//��¼ÿ��������ķ�������ƽ��ǰ����������Ӧ�ĵ�ķ�����������ɵ�Map��
			      			//��ȡ��ǰ������Ӧ��ķ���������
			      			HashSet<NormalUtil> hsn=hmn.get(tempInxex);
			      			if(hsn==null)
			      			{//�����ϲ������򴴽�
			      				hsn=new HashSet<NormalUtil>();
			      			}
			      			//���˵�ķ��������ӵ�������
			      			//����Normal����д��equals���������ͬ���ķ����������ظ������ڴ˵�
			      			//��Ӧ�ķ�����������
			      			hsn.add(new NormalUtil(vNormal[0],vNormal[1],vNormal[2]));
			      			//�����ϷŽ�HsahMap��
			      			hmn.put(tempInxex, hsn);
			      		}
		      		}
		      		else if(tempsa[1].matches("[0-9]+/[0-9]+")||tempsa[1].matches("[0-9]+/[0-9]+/[0-9]+"))
		      		{//�ļ���ʽΪv/vt����v/vt/vn
		      			int[] index=new int[3];//������������ֵ������
			      		
			      		//�����0�����������������ȡ�˶����XYZ��������	      		
			      		index[0]=Integer.parseInt(tempsa[1].split("/")[0])-1;
			      		float x0=alv.get(3*index[0]);
			      		float y0=alv.get(3*index[0]+1);
			      		float z0=alv.get(3*index[0]+2);
			      		alvResult.add(x0);
			      		alvResult.add(y0);
			      		alvResult.add(z0);		
			      		
			      	    //�����1�����������������ȡ�˶����XYZ��������	  
			      		index[1]=Integer.parseInt(tempsa[2].split("/")[0])-1;
			      		float x1=alv.get(3*index[1]);
			      		float y1=alv.get(3*index[1]+1);
			      		float z1=alv.get(3*index[1]+2);
			      		alvResult.add(x1);
			      		alvResult.add(y1);
			      		alvResult.add(z1);
			      		
			      	    //�����2�����������������ȡ�˶����XYZ��������	
			      		index[2]=Integer.parseInt(tempsa[3].split("/")[0])-1;
			      		float x2=alv.get(3*index[2]);
			      		float y2=alv.get(3*index[2]+1);
			      		float z2=alv.get(3*index[2]+2);
			      		alvResult.add(x2);
			      		alvResult.add(y2); 
			      		alvResult.add(z2);	
			      		
			      		//��¼����Ķ�������
			      		alFaceIndex.add(index[0]);
			      		alFaceIndex.add(index[1]);
			      		alFaceIndex.add(index[2]);
			      		
			      		//ͨ��������������������0-1��0-2�����õ�����ķ�����
			      	    //��0�ŵ㵽1�ŵ������
			      		float vxa=x1-x0;
			      		float vya=y1-y0;
			      		float vza=z1-z0;
			      	    //��0�ŵ㵽2�ŵ������
			      		float vxb=x2-x0;
			      		float vyb=y2-y0;
			      		float vzb=z2-z0;
			      	    //ͨ�������������Ĳ�����㷨����
			      		float[] vNormal=vectorNormal(getCrossProduct
						      			(
						      					vxa,vya,vza,vxb,vyb,vzb
						      			));		      	    
			      		for(int tempInxex:index)
			      		{//��¼ÿ��������ķ�������ƽ��ǰ����������Ӧ�ĵ�ķ�����������ɵ�Map��
			      			//��ȡ��ǰ������Ӧ��ķ���������
			      			HashSet<NormalUtil> hsn=hmn.get(tempInxex);
			      			if(hsn==null)
			      			{//�����ϲ������򴴽�
			      				hsn=new HashSet<NormalUtil>();
			      			}
			      			//���˵�ķ��������ӵ�������
			      			//����Normal����д��equals���������ͬ���ķ����������ظ������ڴ˵�
			      			//��Ӧ�ķ�����������
			      			hsn.add(new NormalUtil(vNormal[0],vNormal[1],vNormal[2]));
			      			//�����ϷŽ�HsahMap��
			      			hmn.put(tempInxex, hsn);
			      		}
			      		
			      		//������������֯��������������б���
			      		//��0���������������
			      		int indexTex=Integer.parseInt(tempsa[1].split("/")[1])-1;
			      		altResult.add(alt.get(indexTex*2));
			      		altResult.add(alt.get(indexTex*2+1));
			      	    //��1���������������
			      		indexTex=Integer.parseInt(tempsa[2].split("/")[1])-1;
			      		altResult.add(alt.get(indexTex*2));
			      		altResult.add(alt.get(indexTex*2+1));
			      	    //��2���������������
			      		indexTex=Integer.parseInt(tempsa[3].split("/")[1])-1;
			      		altResult.add(alt.get(indexTex*2));
			      		altResult.add(alt.get(indexTex*2+1));
			      		}
		      		}   	    	      				
		    } 
		    //���ɶ�������
		    int size=alvResult.size();
		    float[] vXYZ=new float[size];
		    //float max=0;	
		   // for(int i=0;i<size-2;i=i+3)
		   // {
		    	//for(int j=0;j<size-2;j=j+3)
		    	 //{
			     // float  n=(alvResult.get(i)-alvResult.get(j))*(alvResult.get(i)-alvResult.get(j))+(alvResult.get(i+1)-alvResult.get(j+1))*(alvResult.get(i+1)-alvResult.get(j+1))+(alvResult.get(i+2)-alvResult.get(j+2))*(alvResult.get(i+2)-alvResult.get(j+2));	
			     // if(n>max)
			      ///{
			    	 // max=n;  
			      //}
		    	 //}
		   // }
		    //float dist=(float) Math.sqrt(max);
		    for(int i=0;i<size;i++)
		    {
		    	vXYZ[i]=alvResult.get(i);
		    }		    
		    //���ɷ���������
		    float[] nXYZ=new float[alFaceIndex.size()*3];
		    int c=0;
		    for(Integer i:alFaceIndex)
		    {
		    	//���ݵ�ǰ���������Map��ȡ��һ���������ļ���
		    	HashSet<NormalUtil> hsn=hmn.get(i);
		    	//���ƽ��������
		    	float[] tn=NormalUtil.getAverage(hsn);	
		    	//���������ƽ����������ŵ�������������
		    	nXYZ[c++]=tn[0];
		    	nXYZ[c++]=tn[1];
		    	nXYZ[c++]=tn[2];
		    }
		    
		    //������������
		    size=altResult.size();
		    float[] tST=new float[size];
		    for(int i=0;i<size;i++)
		    {
		    	tST[i]=altResult.get(i);
		    }
		    //����3D�������
		    if(MainActivity.getLighted()==true)
		        model=new Model(vXYZ,nXYZ);
		    else if(MainActivity.getTextured()==true&&tST!=null)
		    {
		    	model=new Model(vXYZ,nXYZ,tST);
		    	setSTFlag(true);
		    }
		    else if(MainActivity.getTunneled()==true&&tST!=null)
		    {
		    	model=new Model(vXYZ,tST);
		    	setSTFlag(true);
		    }
		    else if(MainActivity.getTwisted()==true&&tST!=null)
		    {
		    	model=new Model(vXYZ,tST);	  
		    	setSTFlag(true);
		    }
		    else if(MainActivity.getMandeled()==true&&tST!=null)
			{
		    	model=new Model(vXYZ,tST);	 
		    	setSTFlag(true);
			}
		    else 
		    {
		    	 model=new Model(vXYZ,nXYZ);
		    }
    	}
    	catch(Exception e)
    	{
    		Log.d("load error", "load error");
    		e.printStackTrace();
    	}    	
    	return model;
    }

    
    public static Model loadFromFile(BufferedReader br)
    {
    	//���غ����������
    	Model model=null;
    	//ԭʼ���������б�--ֱ�Ӵ�obj�ļ��м���
    	ArrayList<Float> alv=new ArrayList<Float>();
    	//������װ�������б�--���������Ϣ���ļ��м���
    	ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();
    	//������������б�--������֯��
    	ArrayList<Float> alvResult=new ArrayList<Float>();    	
    	//ƽ��ǰ����������Ӧ�ĵ�ķ���������Map
    	//��HashMap��keyΪ��������� valueΪ�����ڵĸ�����ķ������ļ���
    	HashMap<Integer,HashSet<NormalUtil>> hmn=new HashMap<Integer,HashSet<NormalUtil>>();  
    	//ԭʼ���������б�
    	ArrayList<Float> alt=new ArrayList<Float>();  
    	//�����������б�
    	ArrayList<Float> altResult=new ArrayList<Float>();  
    	try
    	{
    		String temps=null;
    		//ɨ���ļ������������͵Ĳ�ִͬ�в�ͬ�Ĵ����߼�
		    while((temps=br.readLine())!=null) 
		    {
		    	//�ÿո�ָ����еĸ�����ɲ���
		    	String[] tempsa=temps.split("[ ]+");
		      	if(tempsa[0].trim().equals("v"))
		      	{//����Ϊ��������
		      	    //��Ϊ��������������ȡ���˶����XYZ�������ӵ�ԭʼ���������б���
		      		//float[] vectorV=new float[]{Float.parseFloat(tempsa[1]),Float.parseFloat(tempsa[2]),Float.parseFloat(tempsa[3])};
		      		//float[] v=vectorNormal(vectorV);
		      		alv.add(Float.parseFloat(tempsa[1]));
		      		alv.add(Float.parseFloat(tempsa[2]));
		      		alv.add(Float.parseFloat(tempsa[3]));
		      	}
		    	else if(tempsa[0].trim().equals("vt"))
		      	{//����Ϊ����������
		      		//��Ϊ��������������ȡST���겢���ӽ�ԭʼ���������б���
		      		alt.add(Float.parseFloat(tempsa[1])/2f);
		      		alt.add(Float.parseFloat(tempsa[2])/2f); 
		      	}
		      	else if(tempsa[0].trim().equals("f"))
		      	{//����Ϊ��������
		      		/*
		      		 *��Ϊ��������������� �����Ķ����������ԭʼ���������б���
		      		 *��ȡ��Ӧ�Ķ�������ֵ���ӵ�������������б��У�ͬʱ��������
		      		 *�����������������ķ����������ӵ�ƽ��ǰ����������Ӧ�ĵ�
		      		 *�ķ�����������ɵ�Map��
		      		*/
		      		//�ļ���ʽΪ��f: v����f: v//vn
		      		if(tempsa[1].matches("[0-9]+")||tempsa[1].matches("[0-9]+//[0-9]+"))
		      		{
		      			int[] index=new int[3];//������������ֵ������
			      		
			      		//�����0�����������������ȡ�˶����XYZ��������	      		
			      		index[0]=Integer.parseInt(tempsa[1].split("/")[0])-1;
			      		float x0=alv.get(3*index[0]);
			      		float y0=alv.get(3*index[0]+1);
			      		float z0=alv.get(3*index[0]+2);
			      		//float[] vectorV=new float[]{x0,y0,z0};
			      		//float[] v=vectorNormal(vectorV);
			      		alvResult.add(x0);
			      		alvResult.add(y0);
			      		alvResult.add(z0);		
			      		
			      	    //�����1�����������������ȡ�˶����XYZ��������	  
			      		index[1]=Integer.parseInt(tempsa[2].split("/")[0])-1;
			      		float x1=alv.get(3*index[1]);
			      		float y1=alv.get(3*index[1]+1);
			      		float z1=alv.get(3*index[1]+2);
			      		
			      		alvResult.add(x1);
			      		alvResult.add(y1);
			      		alvResult.add(z1);
			      		
			      	    //�����2�����������������ȡ�˶����XYZ��������	
			      		index[2]=Integer.parseInt(tempsa[3].split("/")[0])-1;
			      		float x2=alv.get(3*index[2]);
			      		float y2=alv.get(3*index[2]+1);
			      		float z2=alv.get(3*index[2]+2);
			      		alvResult.add(x2);
			      		alvResult.add(y2); 
			      		alvResult.add(z2);	
			      		
			      		//��¼����Ķ�������
			      		alFaceIndex.add(index[0]);
			      		alFaceIndex.add(index[1]);
			      		alFaceIndex.add(index[2]);
			      		
			      		//ͨ��������������������0-1��0-2�����õ�����ķ�����
			      	    //��0�ŵ㵽1�ŵ������
			      		float vxa=x1-x0;
			      		float vya=y1-y0;
			      		float vza=z1-z0;
			      	    //��0�ŵ㵽2�ŵ������
			      		float vxb=x2-x0;
			      		float vyb=y2-y0;
			      		float vzb=z2-z0;
			      	    //ͨ�������������Ĳ�����㷨����
			      		float[] vNormal=vectorNormal(getCrossProduct
						      			(
						      					vxa,vya,vza,vxb,vyb,vzb
						      			));
			      	    
			      		for(int tempInxex:index)
			      		{//��¼ÿ��������ķ�������ƽ��ǰ����������Ӧ�ĵ�ķ�����������ɵ�Map��
			      			//��ȡ��ǰ������Ӧ��ķ���������
			      			HashSet<NormalUtil> hsn=hmn.get(tempInxex);
			      			if(hsn==null)
			      			{//�����ϲ������򴴽�
			      				hsn=new HashSet<NormalUtil>();
			      			}
			      			//���˵�ķ��������ӵ�������
			      			//����Normal����д��equals���������ͬ���ķ����������ظ������ڴ˵�
			      			//��Ӧ�ķ�����������
			      			hsn.add(new NormalUtil(vNormal[0],vNormal[1],vNormal[2]));
			      			//�����ϷŽ�HsahMap��
			      			hmn.put(tempInxex, hsn);
			      		}
		      		}
		      		else if(tempsa[1].matches("[0-9]+/[0-9]+")||tempsa[1].matches("[0-9]+/[0-9]+/[0-9]+"))
		      		{//�ļ���ʽΪv/vt����v/vt/vn
		      			int[] index=new int[3];//������������ֵ������
			      		
			      		//�����0�����������������ȡ�˶����XYZ��������	      		
			      		index[0]=Integer.parseInt(tempsa[1].split("/")[0])-1;
			      		float x0=alv.get(3*index[0]);
			      		float y0=alv.get(3*index[0]+1);
			      		float z0=alv.get(3*index[0]+2);
			      		alvResult.add(x0);
			      		alvResult.add(y0);
			      		alvResult.add(z0);		
			      		
			      	    //�����1�����������������ȡ�˶����XYZ��������	  
			      		index[1]=Integer.parseInt(tempsa[2].split("/")[0])-1;
			      		float x1=alv.get(3*index[1]);
			      		float y1=alv.get(3*index[1]+1);
			      		float z1=alv.get(3*index[1]+2);
			      		alvResult.add(x1);
			      		alvResult.add(y1);
			      		alvResult.add(z1);
			      		
			      	    //�����2�����������������ȡ�˶����XYZ��������	
			      		index[2]=Integer.parseInt(tempsa[3].split("/")[0])-1;
			      		float x2=alv.get(3*index[2]);
			      		float y2=alv.get(3*index[2]+1);
			      		float z2=alv.get(3*index[2]+2);
			      		alvResult.add(x2);
			      		alvResult.add(y2); 
			      		alvResult.add(z2);	
			      		
			      		//��¼����Ķ�������
			      		alFaceIndex.add(index[0]);
			      		alFaceIndex.add(index[1]);
			      		alFaceIndex.add(index[2]);
			      		
			      		//ͨ��������������������0-1��0-2�����õ�����ķ�����
			      	    //��0�ŵ㵽1�ŵ������
			      		float vxa=x1-x0;
			      		float vya=y1-y0;
			      		float vza=z1-z0;
			      	    //��0�ŵ㵽2�ŵ������
			      		float vxb=x2-x0;
			      		float vyb=y2-y0;
			      		float vzb=z2-z0;
			      	    //ͨ�������������Ĳ�����㷨����
			      		float[] vNormal=vectorNormal(getCrossProduct
						      			(
						      					vxa,vya,vza,vxb,vyb,vzb
						      			));		      	    
			      		for(int tempInxex:index)
			      		{//��¼ÿ��������ķ�������ƽ��ǰ����������Ӧ�ĵ�ķ�����������ɵ�Map��
			      			//��ȡ��ǰ������Ӧ��ķ���������
			      			HashSet<NormalUtil> hsn=hmn.get(tempInxex);
			      			if(hsn==null)
			      			{//�����ϲ������򴴽�
			      				hsn=new HashSet<NormalUtil>();
			      			}
			      			//���˵�ķ��������ӵ�������
			      			//����Normal����д��equals���������ͬ���ķ����������ظ������ڴ˵�
			      			//��Ӧ�ķ�����������
			      			hsn.add(new NormalUtil(vNormal[0],vNormal[1],vNormal[2]));
			      			//�����ϷŽ�HsahMap��
			      			hmn.put(tempInxex, hsn);
			      		}
			      		
			      		//������������֯��������������б���
			      		//��0���������������
			      		int indexTex=Integer.parseInt(tempsa[1].split("/")[1])-1;
			      		altResult.add(alt.get(indexTex*2));
			      		altResult.add(alt.get(indexTex*2+1));
			      	    //��1���������������
			      		indexTex=Integer.parseInt(tempsa[2].split("/")[1])-1;
			      		altResult.add(alt.get(indexTex*2));
			      		altResult.add(alt.get(indexTex*2+1));
			      	    //��2���������������
			      		indexTex=Integer.parseInt(tempsa[3].split("/")[1])-1;
			      		altResult.add(alt.get(indexTex*2));
			      		altResult.add(alt.get(indexTex*2+1));
			      		}
		      		}   	    	      				
		    } 
		    //���ɶ�������
		    int size=alvResult.size();
		    float[] vXYZ=new float[size];
		   // float max=0;	
		   // for(int i=0;i<size-2;i=i+3)
		    //{
		    	//for(int j=0;j<size-2;j=j+3)
		    	// {
			     // float  n=(alvResult.get(i)-alvResult.get(j))*(alvResult.get(i)-alvResult.get(j))+(alvResult.get(i+1)-alvResult.get(j+1))*(alvResult.get(i+1)-alvResult.get(j+1))+(alvResult.get(i+2)-alvResult.get(j+2))*(alvResult.get(i+2)-alvResult.get(j+2));	
			     // if(n>max)
			    	 //max=n; 
		    	 //}
		   // }
		    //float dist=(float) Math.sqrt(max);
		    for(int i=0;i<size;i++)
		    {
		    	vXYZ[i]=alvResult.get(i);
		    }
		    
		    //���ɷ���������
		    float[] nXYZ=new float[alFaceIndex.size()*3];
		    int c=0;
		    for(Integer i:alFaceIndex)
		    {
		    	//���ݵ�ǰ���������Map��ȡ��һ���������ļ���
		    	HashSet<NormalUtil> hsn=hmn.get(i);
		    	//���ƽ��������
		    	float[] tn=NormalUtil.getAverage(hsn);	
		    	//���������ƽ����������ŵ�������������
		    	nXYZ[c++]=tn[0];
		    	nXYZ[c++]=tn[1];
		    	nXYZ[c++]=tn[2];
		    }
		    
		    //������������
		    size=altResult.size();
		    float[] tST=new float[size];
		    for(int i=0;i<size;i++)
		    {
		    	tST[i]=altResult.get(i);
		    }
		    //����3D�������
		    if(MainActivity.getLighted()==true)
		        model=new Model(vXYZ,nXYZ);
		    else if(MainActivity.getTextured()==true&&tST!=null)
		    {
		    	model=new Model(vXYZ,nXYZ,tST);
		    	setSTFlag(true);
		    }
		    else if(MainActivity.getTunneled()==true&&tST!=null)
		    {
		    	model=new Model(vXYZ,tST);
		    	setSTFlag(true);
		    }
		    else if(MainActivity.getTwisted()==true&&tST!=null)
		    {
		    	model=new Model(vXYZ,tST);	  
		    	setSTFlag(true);
		    }
		    else if(MainActivity.getMandeled()==true&&tST!=null)
			{
		    	model=new Model(vXYZ,tST);	 
		    	setSTFlag(true);
			}
		    else
		    {
		    	 model=new Model(vXYZ,nXYZ);
		    }
    	}
    	catch(Exception e)
    	{
    		Log.d("load error", "load error");
    		e.printStackTrace();
    	}    	
    	return model;
    }

    
}
package com.tu.tutest.tutest_test1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;



public class TuTest {
	
	@Test
	public void test1(/*HttpServletRequest request, HttpServletResponse response*/){
		System.out.println("------tuzhaoliang-Test-------");
		JSONObject json = new JSONObject();
		json.put("deviceType", 0);
		json.put("area", 320000);
		json.put("city", 320200);
		json.put("province", 320000);
		json.put("userId", 241);
		json.put("deviceProperty", 0);
		json.put("workStatus", 1);
		String result = httpPostWithJson(json, "http://e-meter.cmiotcd.com/rsms_2.0/meterdevice/searchDevice", "002feef400fc669eaa8270e9844c9d6e");
		System.out.print(result);
	}
	
	public String httpPostWithJson(JSONObject jsonObj,String url,String token){
	    HttpPost post = null;
	    try {
	        HttpClient httpClient = new DefaultHttpClient();
	        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
	        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
	        post = new HttpPost(url);
	        post.setHeader("Content-type", "application/json; charset=utf-8");
	        post.setHeader("token", token);
	        StringEntity entity = new StringEntity(jsonObj.toString(), Charset.forName("UTF-8"));
	        entity.setContentEncoding("UTF-8");
	        entity.setContentType("application/json");
	        post.setEntity(entity);
	        HttpResponse response = httpClient.execute(post);
	        String result = "";
	        if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                	result = EntityUtils.toString(resEntity,"UTF-8");
                }  
            }
	        return result;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "";
	    }
	}
	
	@Test
	public void test2(){
		String s1 = "tzl";
		String s2 = null;
		String s3 = s1 + s2;
		System.out.println(s3);
	}
	
	@Test
	public void test3() throws IOException{
		long startNumber = 13906770001L;
		long number = 9998L;
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\Administrator\\Desktop\\电话号码.txt"));
		
		for(int i=1;i<number;i++){
			bw.write(startNumber+i+"\n");
		}
		
		bw.flush();
		bw.close();
	}
	
	@Test
	public void test4() throws IOException
	{
		File in = new File("C:\\Users\\Administrator\\Desktop\\arc.txt");
		File out = new File("C:\\Users\\Administrator\\Desktop\\arc1.txt");
		BufferedReader br = new BufferedReader(new FileReader(in));
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line = "";
		while((line = br.readLine()) != null)
		{
			//A地块.D区.2楼.D12006-A
			String[] split = line.split("\\.");
			bw.write(split[3]+"\n");
		}
		
		br.close();
		
		bw.flush();
		bw.close();
	}
	
	@Test
	public void test5() throws SQLException
	{
		List<Integer> ammeterIDs = new ArrayList<Integer>();
		
		//查询所有AMMETER_ID
		String sql = "select AMMETER_ID, from AMMETER";
		PreparedStatement ps= null;
		ResultSet rs =null;
		Connection conn=null;
		
		conn=getConnection();
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		
		while(rs.next())
		{
			ammeterIDs.add(rs.getInt("AMMETER_ID"));
		}
		//查询所有ZAMDATAS+AMMETER_ID 的电表的终止值
	}
	
	private void test6() throws SQLException, IOException
	{
		List<MeterModel> models = new ArrayList<MeterModel>();
		
		//查询所有AMMETER_ID
		String sql = "select AMMETER_ID from AMMETER";
		//查电表终止示数
		String sql1 = "";
		//查剩余电量
		String sql2 = "";//"SELECT * from AMMETERAPDATAS WHERE AMMETER_ID = 42 AND ROWNUM=1 ORDER BY VALUETIME DESC ";
		//查询总购电量
		String sql3= "";
		
		PreparedStatement ps= null;
		ResultSet rs =null;
		Connection conn=null;
		
		conn=getConnection();
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		
		while(rs.next())
		{
			//查询所有AMMETER_ID
			int ammeterID = rs.getInt("AMMETER_ID");
			
			if(ammeterID == 90) continue;
			if(ammeterID == 402) continue;
			
			MeterModel model = new MeterModel();
			model.setAmmeterID(ammeterID);
			//查电表终止示数
			sql1 = "SELECT ZVALUEZY FROM zamdatas"+ammeterID+" WHERE ROWNUM=1 ORDER BY valueTime DESC";
			PreparedStatement ps1 = conn.prepareStatement(sql1);
			ResultSet rs1 = ps1.executeQuery();
			model.setTerminalNum(rs1.getDouble("ZVALUEZY"));
			//查剩余电量
			sql2 = "SELECT SYVALUE from AMMETERAPDATAS WHERE AMMETER_ID ="+ ammeterID +" AND ROWNUM=1 ORDER BY VALUETIME DESC";
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			ResultSet rs2 = ps2.executeQuery();
			model.setSYValue(rs2.getDouble("SYVALUE"));
			//查询总购电量
			sql3 = "SELECT AMMETER_ID,AMMETER_NAME, SUM(THEGROSS) AS SUM FROM APSALEINFO B WHERE AMMETER_ID="+ammeterID+" GROUP BY AMMETER_ID,AMMETER_NAME ORDER BY AMMETER_ID;";
			PreparedStatement ps3 = conn.prepareStatement(sql3);
			ResultSet rs3 = ps3.executeQuery();
			model.setGross(rs3.getDouble("SUM"));
			model.setAmmeterName(rs3.getString("AMMETER_NAME"));
			
			models.add(model);
		}
		
		//写到csv文件
		File out = new File("C:\\Users\\Administrator\\Desktop\\ssdf.csv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line = "";
		bw.write("电表id,电表name,终止电量,剩余电量,购买量\n");
		for(int i=0;i<models.size();i++)
		{
			MeterModel m = models.get(i);
			line = m.getAmmeterID()+","+m.getAmmeterName()+","+m.getTerminalNum()+","+m.getSYValue()+","+m.getGross()+"\n";
			bw.write(line);
		}
		
		
		bw.flush();
		bw.close();
		
		
		
	}
	
	
	public static Connection getConnection() throws SQLException
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		} catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn = null;
		try
		{
			conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.7.176:1521:orcl", "VESSCHOOL1760312", "sa_123456");
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
}

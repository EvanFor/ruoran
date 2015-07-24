package com.cd.common.service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cd.common.CountChangeListener;
import com.cd.common.UserTemplateInterface;
import com.cd.common.dao.UserDao;
import com.cd.common.domain.User;
import com.cd.common.util.Page;

@Service
public class UserService implements UserTemplateInterface
{
	@Autowired
	private UserDao userDao;
	
	public Page<User> queryForPage(int pageNum, int pageSize)
	{
		return userDao.queryForPage(pageNum, pageSize);
	}
	
	public User find(Integer id)
	{
		return userDao.find(id);
	}
	
	public User add(User user) 
	{
		return userDao.add(user);
	}
	
	public User delete(Integer id) 
	{
		return userDao.delete(id);
	}
	
	public User update(User user) 
	{
		return userDao.update(user);
	}
	
	@Override
	public Long count()
	{
		return userDao.count();
	}
	
	public void setCountChangeListener(CountChangeListener listener)
	{
		this.userDao.setCountChangeListener(listener);
	}
	
	public ByteArrayOutputStream exportUsers(int pageNum, int pageSize, int export)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Page<User> data = export == 0 ? userDao.queryForPage(pageNum, pageSize) : userDao.queryForAll();
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("用户列表");
		sheet.setDefaultColumnWidth(15);
		
		HSSFRow row = sheet.createRow(0);
		
		String[] headers = { "姓名", "生日", "年龄", "性别", "婚否", "存款", "薪资" };
		for (int i = 0; i < headers.length; i++)
		{
			HSSFCell cell = row.createCell(i);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		
		int index = 0;
		for (User user : data.getResult())
		{
			index++;
			row = sheet.createRow(index);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue(user.getName());
			cell = row.createCell(1);
			cell.setCellValue(format.format(user.getBirthday()));
			cell = row.createCell(2);
			cell.setCellValue(user.getAge());
			cell = row.createCell(3);
			cell.setCellValue(user.getGender());
			cell = row.createCell(4);
			cell.setCellValue(user.getMarried());
			cell = row.createCell(5);
			cell.setCellValue(user.getMoney());
			cell = row.createCell(6);
			cell.setCellValue(user.getSalary());
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try
		{
			workbook.write(baos);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return baos;
	}
	
	public List<User> importUsers(InputStream inputStream) throws FileNotFoundException, IOException, ParseException
	{
		List<User> addUsers = new ArrayList<User>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.rowIterator();
		rows.next();
		while (rows.hasNext())
		{
			HSSFRow row = (HSSFRow) rows.next();
			User user = new User();
			user.setId(Float.valueOf(row.getCell(0).toString()).intValue());
			user.setName(row.getCell(1).toString());
			user.setBirthday(format.parse(row.getCell(2).toString()));
			user.setAge(Float.valueOf(row.getCell(3).toString()).intValue());
			userDao.add(user);
			addUsers.add(user);
		}
		return addUsers;
	}
	
	@Override
	public User login(User user) 
	{
		return userDao.login(user);
	}
	
	public void setUserDao(UserDao userDao)
	{
		this.userDao = userDao;
	}
}

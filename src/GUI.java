import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;

import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.translate.demo.Translated;


//注释后上传 github！！！！！
public class GUI extends JFrame implements ActionListener{
	private JPanel panel;
	private JTextField original;
	private JComboBox<String> comboBox;
	private JPanel result_panel,result_center;
	private boolean isTranslation,isDelivery,isWeather;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	
	//GUI初始化
	public GUI()
	{
		super("查询小工具");
		this.getContentPane().setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBackground(Color.DARK_GRAY);
		
		isTranslation=true;//默认初始是 翻译 
		isDelivery=false;
		isWeather=false;
	}
	
	//在窗体上添加面板，显示更多组件
	public void addPanel()
	{
		panel=new JPanel(new FlowLayout());
		panel.setBackground(Color.DARK_GRAY);
		
		String[] strList={"翻译","快递","天气"};
		comboBox=new JComboBox<String>(strList);
		comboBox.setFont(new Font("微软雅黑",1,15));
		comboBox.setSelectedIndex(0);
		comboBox.addActionListener(this);
		panel.add(comboBox);
		
		JPanel inputArea=new JPanel();
		inputArea.setBackground(Color.DARK_GRAY);
		original=new JTextField(20);
		original.setFont(new Font("微软雅黑",0,18));
		original.setBackground(Color.white);
		
		inputArea.add(original);
		
		JButton buttonEnter=new JButton("查询");
		buttonEnter.setFont(new Font("微软雅黑",1,15));
		buttonEnter.addActionListener(this);
		this.getRootPane().setDefaultButton(buttonEnter);//按回车等效于点击按钮
		inputArea.add(buttonEnter);
		
		panel.add(inputArea);
		
		this.getContentPane().add(panel,BorderLayout.NORTH);
		
		result_panel=new JPanel(new BorderLayout());
		result_panel.setBackground(Color.DARK_GRAY);
		
		Icon result_icon=new ImageIcon("result.png");
		JLabel result_head=new JLabel("查询结果：",result_icon,JLabel.LEFT);
		result_head.setFont(new Font("微软雅黑",1,15));
		result_head.setForeground(Color.GRAY);
		result_panel.add(result_head, BorderLayout.NORTH);
		
		textArea=new JTextArea();
		textArea.setBackground(Color.DARK_GRAY);
		textArea.setFont(new Font("微软雅黑",0,18));
		textArea.setForeground(Color.WHITE);
		textArea.setLineWrap(true);//自动换行
		
		scrollPane=new JScrollPane(textArea);//增加滚动条，用于查询快递时显示较多的信息
		result_panel.add(scrollPane,BorderLayout.CENTER);
		
		this.getContentPane().add(result_panel,BorderLayout.CENTER);

		
		this.setSize(600, 300);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		original.grabFocus();//在初始状态，用户不用点击可直接输入文字
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource().equals(comboBox))
		{
			//用户要查询的类别
			if(comboBox.getSelectedItem().toString().equals("翻译")) 
			{
				isTranslation=true;
				isDelivery=false;
				isWeather=false;
			}
			else if(comboBox.getSelectedItem().toString().equals("快递"))
			{
				isTranslation=false;
				isDelivery=true;
				isWeather=false;
			}
			else if(comboBox.getSelectedItem().toString().equals("天气"))
			{
				isTranslation=false;
				isDelivery=false;
				isWeather=true;
			}
		}
		else
		{
			//将调用API得到的结果显示在面板上
			if(isTranslation) showTranslation();
			else if(isDelivery) showDelivery();
			else if(isWeather) showWeather();
		}	
	}
	
	public void showTranslation()
	{
		String text=original.getText();
		String ans="       ";//为使界面好看，增加几个空格
		
		if(!text.isEmpty())
		{
			try 
			{
				Translated translated=new Translated();
				ans+=translated.getResult(text);
			} 
			catch (UnsupportedEncodingException e1) { e1.printStackTrace(); } 
			catch (JSONException e1) { e1.printStackTrace(); }
		
			textArea.setText(ans);
			
		}
	}
	
	public void showDelivery()
	{
		String[] query=original.getText().split(" ");
		if(query.length!=2) 
		{
			textArea.setText("很抱歉，查无数据");
			return;
		}
		
		//org是快递公司编码，code是快递单号
		String org=query[0]; String code=query[1];
		
		KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
		try {
			
			//获取快递轨迹（JSONArray格式）
			String result = api.getOrderTracesByJson(org, code);
			JSONObject result_json=new JSONObject(result);
			JSONArray result_traces=result_json.getJSONArray("Traces");
			
			if(result_traces.length()==0) 
			{
				textArea.setText("很抱歉，查无数据");
				return;
			}
			
			
			textArea.setText("");
			//因用户主要想看最新的快递信息，所以按时间顺序逆序显示轨迹信息
			for(int i=result_traces.length()-1;i>=0;i--)
			{
				String station,time;
				station=result_traces.getJSONObject(i).getString("AcceptStation");
				time=result_traces.getJSONObject(i).getString("AcceptTime");
				textArea.append("•"+time+"\n"+station+"\n\n");
				
			}
			textArea.setCaretPosition(0);//定位到第一条（最新）轨迹信息
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public void showWeather()
	{
		Weather weather = new Weather();
        String url=new String();
        try {
        	 //将用户输入的城市名称转化为拼音	
        	 String location=new String(original.getText());
        	 PinyinTool tool=new PinyinTool();
        	 String location_pinyin=tool.toPinYin(location, "", PinyinTool.Type.LOWERCASE);
        
        	 //生成请求地址
             url = weather.generateGetDiaryWeatherURL(
            		location_pinyin,
                    "zh-Hans",
                    "c",
                    "0",
                    "3"
            );
             
            UrlReqUtil uru=new UrlReqUtil();
            boolean success=uru.get(url);
            
            if(success)
            {
            	JSONArray result=uru.getResult();
            	JSONObject result0=result.getJSONObject(0);
            	JSONObject result1=result.getJSONObject(1);
            	JSONObject result2=result.getJSONObject(2);
            	
            	textArea.setText("今日天气:\n");
            	textArea.append("白天："+result0.getString("text_day")+"   晚间："+result0.getString("text_night")
            			        +"   气温："+result0.getString("low")+"℃ ~ "+result0.getString("high")+"℃\n\n");
            	
            	textArea.append("未来两天天气：\n");
            	textArea.append("白天："+result1.getString("text_day")+"   晚间："+result1.getString("text_night")
    			        +"   气温："+result1.getString("low")+"℃ ~ "+result1.getString("high")+"℃\n");
            	textArea.append("白天："+result2.getString("text_day")+"   晚间："+result2.getString("text_night")
    			        +"   气温："+result2.getString("low")+"℃ ~ "+result2.getString("high")+"℃\n");
            }
            else
            {
            	textArea.setText("很抱歉，查无数据");
            	return;
            }
            
        } catch (Exception e) { System.out.println("Exception:" + e); }
	}
	
	
	public static void createAndShowGUI()
	{
		GUI gui=new GUI();
		gui.addPanel();
		
	}
	
	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				createAndShowGUI();
			}
		});
	}
}

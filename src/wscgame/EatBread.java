package wscgame;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
import java.applet.*;
import java.net.MalformedURLException;
import java.net.URL;

public class EatBread extends JPanel
{
	public static final int WIDTH = 415;  //窗口宽
	public static final int HEIGHT = 700; //窗口高
	
	public static BufferedImage background; //背景图
	public static BufferedImage start;   	//启动图
	public static BufferedImage pause;		//暂停图
	public static BufferedImage gameover;	//游戏结束图
	public static BufferedImage bread;		//面包
	public static BufferedImage superman;	//王思聪
	public static BufferedImage bread0;		//面包0
	public static BufferedImage superman0;	//王思聪0
	
	static{ //初始化静态图片
		try
		{
			background = ImageIO.read(EatBread.class.getResource("background.png"));
			start = ImageIO.read(EatBread.class.getResource("start.png"));
			pause = ImageIO.read(EatBread.class.getResource("pause.png"));
			gameover = ImageIO.read(EatBread.class.getResource("gameover.png"));
			bread = ImageIO.read(EatBread.class.getResource("bread.png"));
			superman = ImageIO.read(EatBread.class.getResource("superman.png"));
			bread0 = ImageIO.read(EatBread.class.getResource("bread0.png"));
			superman0 = ImageIO.read(EatBread.class.getResource("superman0.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private Superman supreme = new Superman(); 	//一个王思聪
	private FlyingObject[] droppingbread = {}; 	//一堆面包
	private Bread0 bread_0 = new Bread0();
	private Superman0 supreme0 = new Superman0();
	static PlayMusic playbgm = new PlayMusic("bgm.mp3");
	static PlayMusic startbgm = new PlayMusic("start.wav");
	
	
	public static final int START = 0;     		//启动状态
	public static final int RUNNING = 1;   		//运行状态
	public static final int PAUSE = 2;     		//暂停状态
	public static final int GAME_OVER = 3; 		//游戏结束状态
	public static final int DUANG = 4;			//技能发动状态
	private int state = START; 					//当前状态(默认启动状态)
	

	
	/** 生成面包对象 */
	public FlyingObject nextOne()
	{
			return new DropBread();	
	}
	
	int score = 0; //玩家的得分
	
	public void action(){
		//创建鼠标侦听器
		MouseAdapter l = new MouseAdapter()
		{
			/** 重写mouseMoved()鼠标移动事件 */
			public void mouseMoved(MouseEvent e)
			{
				if(state==RUNNING||state==DUANG)
				{ 											//运行状态下执行
					int x = e.getX(); 						//获取鼠标的x坐标
					int y = e.getY(); 						//获取鼠标的y坐标
					supreme.moveTo(x, y); 					//王思聪随着鼠标移动
				}
			}
			/** 重写mouseClicked()鼠标点击事件 */
			public void mouseClicked(MouseEvent e){
				switch(state){ 								//根据当前状态做不同的操作
				case START:        							//启动状态时
					state=RUNNING; 							//修改为运行状态
					startbgm.play();
					playMusic("01.wav");
					break;
				case GAME_OVER:  							//游戏结束状态时
					score = 0;   							//清理现场
					supreme = new Superman();
					droppingbread = new FlyingObject[0];
					state=START; 							//修改为启动状态
					break;
				case RUNNING:
					if(e.getClickCount()==3)
					{
						state = DUANG;
					}
				case DUANG:
				{
					if(e.getClickCount()==2)
					{
						state = RUNNING;
					}
				}
				}
			}
			/** 重写mouseExited()鼠标移出事件 */
			public void mouseExited(MouseEvent e)
			{
				if(state==RUNNING)
				{ 											//运行状态时
					state=PAUSE;    						//修改为暂停状态
				}
			}
			/** 重写mouseEntered()鼠标移入事件 */
			public void mouseEntered(MouseEvent e)
			{
				if(state==PAUSE)
				{  											//暂停状态时
					state=RUNNING; 							//修改为运行状态
				}
			}
		};
		this.addMouseListener(l); 							//处理鼠标操作事件
		this.addMouseMotionListener(l); 					//处理鼠标滑动事件
		
		Timer timer = new Timer(); 							//创建定时器对象
		int intervel = 10; 									//时间间隔(以毫秒为单位)
		timer.schedule(new TimerTask()
		{
			public void run()
			{ 												//定时干的那个事--10毫秒走一次
				if(state==RUNNING)
				{ 											//运行状态时执行
					enterAction(); 							//面包入场
					stepAction();  							//飞行物面包、王思聪走一步
					outOfBoundsAction(); 					//删除越界的飞行物(面包)
					hitAction();   							//王思聪与面包的碰撞
					checkGameOverAction(); 					//判断游戏结束
				}
				if(state==DUANG)
				{ 											//运行状态时执行
					enterAction(); 							//面包入场
					duangAction();  							//飞行物面包、王思聪走一步
					outOfBoundsAction(); 					//删除越界的飞行物(面包)
					hitAction();   							//王思聪与面包的碰撞
					checkGameOverAction(); 					//判断游戏结束
				}
				repaint(); 									//重画(调用paint()方法)
			}
		},intervel,intervel); 								//定时计划
	}

	public void paint(Graphics g)
	{
		g.drawImage(background,0,0,null); 			//画背景图
		paintBread0(g);								//画面包0
		paintSuperman0(g);							//画王思聪0
		paintScoreAndLife(g); 						//画分和画命
		paintSuperman(g); 							//画王思聪对象
		paintBread(g); 								//画面包对象
		paintState(g); 								//画状态
	}
	
	/** 画王思聪对象 */
	public void paintSuperman(Graphics g)
	{
		g.drawImage(supreme.image,supreme.x,supreme.y,null); 	//画王思聪对象
	}
	
	/** 画王思聪0对象 */
	public void paintSuperman0(Graphics g)
	{
		g.drawImage(supreme0.image,supreme0.x,supreme0.y,null); 	//画王思聪0对象
	}

	/** 画面包0对象 */
	public void paintBread0(Graphics g)
	{
		g.drawImage(bread_0.image,bread_0.x,bread_0.y,null); 	//画王思聪对象
	}
	
	/** 画面包对象 */
	public void paintBread(Graphics g)
	{
		for(int i=0;i<droppingbread.length;i++)
		{ 														//遍历所有面包
			FlyingObject f = droppingbread[i]; 					//获取每一个面包
			g.drawImage(f.image,f.x,f.y,null); 					//画面包
		}
	}
	
	/** 画分和画命 */
	public void paintScoreAndLife(Graphics g)
	{
		g.setColor(new Color(0x000000)); 						//设置颜色
		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,24)); 		//设置字体样式(字体:SANS_SERIF 样式:BOLD加粗  字号:24)
		g.drawString(" X "+score,80,85);
		g.drawString(" X "+supreme.getLife(),280,85);
	}
	/** 画状态 */
	public void paintState(Graphics g){
		switch(state){ 											//根据当前状态的不同画不同的图
		case START: 											//启动状态时画启动图
			g.drawImage(start,0,0,null);
			break;
		case PAUSE: 											//暂停状态时画暂停图
			g.drawImage(pause,0,0,null);
			break;
		case GAME_OVER: 										//游戏结束状态时画游戏结束图
			g.drawImage(gameover,0,0,null);
			break;
		}
	}
	
	int flyIndex = 0; //面包入场计数
	/** 面包 =入场 */
	public void enterAction()
	{ 																				//10毫秒走一次
		flyIndex++;																	//每10毫秒增1
		if(flyIndex%60==0)
		{ 																			//400(10*40)毫秒走一次
			FlyingObject obj = nextOne(); 											//获取面包对象
			droppingbread = Arrays.copyOf(droppingbread,droppingbread.length+1); 	//扩容
			droppingbread[droppingbread.length-1] = obj; 							//将面包添加到最后一个元素上
		}
	}
	
	/** 飞行物(面包+王思聪)走一步 */
	public void stepAction()
	{ 																				//10毫秒走一次
		supreme.step(); 															//王思聪走一步
		for(int i=0;i<droppingbread.length;i++)
		{ 																			//遍历所有面包
			droppingbread[i].step(); 												//面包走一步
		}
	}
	
	public void duangAction()
	{
		Timer timer;
		timer = new Timer();
		for(int i=0;i<droppingbread.length;i++)
		{
			if((droppingbread[i].x-supreme.x)>0)
				for(;(droppingbread[i].x-supreme.x)>30;droppingbread[i].x--)
				{
					droppingbread[i].y+=2; 
				}
			if((droppingbread[i].x-supreme.x)<0)
				for(;(droppingbread[i].x-supreme.x)<30;droppingbread[i].x++)
				{
					droppingbread[i].y+=2; 
				}
		}
	}
	
	public class AL implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e) 
		{
			for(int i=0;i<droppingbread.length;i++)
			{
				if((droppingbread[i].x-supreme.x)>0)
					for(;(droppingbread[i].x-supreme.x)>30;droppingbread[i].x--)
					{
						droppingbread[i].y+=2; 
					}
				if((droppingbread[i].x-supreme.x)<0)
					for(;(droppingbread[i].x-supreme.x)<30;droppingbread[i].x++)
					{
						droppingbread[i].y+=2; 
					}
			}
			repaint();
		}
	}
	
	/** 删除越界的飞行物面包*/
	public void outOfBoundsAction()
	{ 																				//10毫秒走一次
		int index = 0; 																//1)不越界面包数组下标  2)不越界面包个数
		FlyingObject[] flyingLives = new FlyingObject[droppingbread.length]; 		//不越界面包数组
		for(int i=0;i<droppingbread.length;i++)
		{ 																			//遍历所有面包
			FlyingObject f = droppingbread[i]; 										//获取每一个面包
			if(!f.outOfBounds())
			{ 																		//不越界
				flyingLives[index] = f; 											//将不越界面包对象添加到不越界面包数组中
				index++; 															//1)不越界面包数组下标增一  2)不越界面包个数增一
			}
			else
			{
				supreme.subtractLife();
			}
		}
		droppingbread = Arrays.copyOf(flyingLives,index); 							//将不越界面包数组复制到flyings中，flyings的长度为index(不越界面包个数)
		
	}
	
	/** 王思聪与面包的碰撞 */
	public void hitAction()
	{																				//10毫秒走一次
		for(int i=0;i<droppingbread.length;i++)
		{ 																			//遍历所有面包
			FlyingObject f = droppingbread[i]; 										//获取每一个面包
			if(supreme.hit(f))
			{ 																		//撞上了
																					//交换被撞的面包与数组中的最后一个元素
				FlyingObject t = droppingbread[i];
				droppingbread[i] = droppingbread[droppingbread.length-1];
				droppingbread[droppingbread.length-1] = t;
																					//缩容(去掉最后一个元素，即被撞的面包对象)
				droppingbread = Arrays.copyOf(droppingbread,droppingbread.length-1);
				
				score += 1;
			}
		}
	}
	
	/** 判断游戏结束 */
	public void checkGameOverAction()
	{ 																				//10毫秒走一次
		if(supreme.getLife()<=0)
		{ 																			//游戏结束了
			state=GAME_OVER;   														//修改当前状态为游戏结束状态
		}
	}

	@SuppressWarnings("deprecation")
	static void playMusic(String filename){//背景音乐播放
		try {
			URL cb;
			File f = new File(filename); // 引号里面的是音乐文件所在的路径
			cb = f.toURI().toURL();
			AudioClip aau;
			aau = Applet.newAudioClip(cb);
		
			//aau.play();	
			aau.loop();//循环播放
			// 循环播放 aau.play()
			//单曲 aau.stop()停止播放
 
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}
	}	

	
	public static void main(String[] args) 
	{
		playbgm.play();
		playMusic("start.wav");
		JFrame frame = new JFrame("Fly"); 			//创建窗口对象
		EatBread game = new EatBread(); 			//创建面板对象
		frame.add(game); 							//将面板添加到窗口中
		frame.setSize(WIDTH, HEIGHT); 				//设置窗口大小
		frame.setAlwaysOnTop(true); 				//设置总是在最上面
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //设置窗口默认关闭操作(关闭窗口时退出程序)
		frame.setLocationRelativeTo(null); 			//设置居中显示
		frame.setVisible(true); 					//1)设置窗口可见  2)尽快调用paint()方法
		
		game.action();	 							//启动程序的执行

	}
}

package wscgame;
import java.util.Random;


public class DropBread extends FlyingObject implements Bread 
{
	Random rand0 = new Random();
	public int speed = rand0.nextInt(6)+4; 					//移动的速度
	/** 构造方法 */
	public DropBread()
	{
		image = EatBread.bread; 							//图片
		width = image.getWidth();   						//宽
		height = image.getHeight(); 						//高
		Random rand = new Random(); 						//随机数对象
		x = rand.nextInt(EatBread.WIDTH-this.width); 		//x:0到(窗口宽-面包宽)之间的随机数
		y = -this.height; 									//y:负的面包的高
	}
	
	/** 重写getScore()得分 */
	public int getScore()
	{
		return 1; 											//打掉一个面包得5分
	}
	
	/** 重写step()走步 */
	public void step()
	{
		y+=speed; 											//y+(向下)
	}
	
	public void moveTo(int x,int y)
	{
		this.x = x-this.width/2; 				
		this.y = y-this.height/2; 			
	}
	
	/** 重写outOfBounds()检查是否越界 */
	public boolean outOfBounds()
	{
		return this.y>=EatBread.HEIGHT; 					//面包的y>=窗口的高，即为越界了
	}
}


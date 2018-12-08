package wscgame;

public class Wang extends Movable {
    //private int index;
    private int lives; //命

    /**
     * 构造方法
     */
    public Wang() {
        image = EatBread.imgWangSiCong; //图片
        width = image.getWidth();   //宽
        height = image.getHeight(); //高
        x = EatBread.WIDTH / 2 - width / 2; //x:固定的值
        y = 495; //y:固定的值
        lives = 3;
        //index = 1;

    }

    public void step() { //10毫秒走一次
        //x += index;
    }

    public void moveTo(int x, int y) {
        this.x = x - this.width / 2;            //王思聪的x=鼠标的x-1/2王思聪的宽
        //this.y = y-this.height/2; 			//王思聪的y=鼠标的y-1/2王思聪的高
    }

    /**
     * 获取王思聪的命
     */
    public int getLives() {
        return lives; //返回命数
    }

    /**
     * 王思聪减命
     */
    public void loseLife() {
        lives--; //命数减1
    }

    /**
     * 重写outOfBounds()检查是否越界
     */
    public boolean outOfBounds() {
        return false; //永不越界
    }

    /**
     * 王思聪与面包的碰撞算法  this:王思聪 other:面包
     */
    public boolean hit(Movable other) {
        int x1 = other.x - this.width / 2;               //x1:面包的x-1/2王思聪的宽
        int x2 = other.x + other.width + this.width / 2;   //x2:面包的x+面包的宽+1/2王思聪的宽
        int y1 = other.y - this.height / 2;             //y1:面包的y-1/2王思聪的高
        int y2 = other.y + other.height + this.height / 2; //y2:面包的y+面包的高+1/2王思聪的高
        int x = this.x + this.width / 2;                 //x:王思聪的x+1/2王思聪的宽
        int y = this.y + this.height / 2;                 //y:王思聪的y+1/2王思聪的高

        return x >= x1 + 70 && x <= x2 - 30
                &&
                y >= y1 + 150 && y <= y2 - 38; //x在x1与x2之间，并且，y在y1与y2之间，即为撞上了
    }

}

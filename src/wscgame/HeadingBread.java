package wscgame;

public class HeadingBread extends Movable {
    public HeadingBread() {
        image = EatBread.imgHeadingBread; //图片
        width = image.getWidth();   //宽
        height = image.getHeight(); //高
        x = 50; //x:固定的值
        y = 50; //y:固定的值
    }

    /**
     * 飞行物走一步
     */
    public void step() {
    }

    /**
     * 检测是否出界
     */
    public boolean outOfBounds() {
        return false;
    }
}

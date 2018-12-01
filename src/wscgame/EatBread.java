package wscgame;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.awt.event.*;

public class EatBread extends JPanel {
    static BufferedImage imgBackground;
    static BufferedImage imgStart;    //启动图
    static BufferedImage imgPause;        //暂停图
    static BufferedImage imgGameOver;    //游戏结束图
    static BufferedImage imgBread;        //面包
    static BufferedImage imgWangSiCong;    //王思聪
    static BufferedImage imgHeadingBread;        //面包0
    static BufferedImage imgHeadingWangSiCong;    //王思聪0
    static MusicPlayer introMusic;
    static MusicPlayer backgroundMusic;

    static {
        try {
            imgBackground = ImageIO.read(EatBread.class.getResource("background.png"));
            imgStart = ImageIO.read(EatBread.class.getResource("start.png"));
            imgPause = ImageIO.read(EatBread.class.getResource("pause.png"));
            imgGameOver = ImageIO.read(EatBread.class.getResource("game_over.png"));
            imgBread = ImageIO.read(EatBread.class.getResource("bread.png"));
            imgWangSiCong = ImageIO.read(EatBread.class.getResource("wang_sicong.png"));
            imgHeadingBread = ImageIO.read(EatBread.class.getResource("heading_bread.png"));
            imgHeadingWangSiCong = ImageIO.read(EatBread.class.getResource("heading_wang_sicong.png"));
            introMusic = new MusicPlayer(EatBread.class.getResource("intro.wav"));
            backgroundMusic = new MusicPlayer(EatBread.class.getResource("bgm.wav"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static final int WIDTH = imgBackground.getWidth();   //窗口宽
    static final int HEIGHT = imgBackground.getHeight(); //窗口高

    private Wang wang = new Wang();          //一个王思聪
    private Movable[] droppingBread = {};    //一堆面包
    private HeadingBread headingBread = new HeadingBread();
    private HeadingWang headingWang = new HeadingWang();


    // Consider enumeration
    public static final int START = 0;            //启动状态
    public static final int RUNNING = 1;          //运行状态
    public static final int PAUSE = 2;            //暂停状态
    public static final int GAME_OVER = 3;        //游戏结束状态
    private int state = START;                    //当前状态(默认启动状态)
    int score = 0; //玩家的得分

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Wang Sicong Eating Bread");          //创建窗口对象
        EatBread game = new EatBread();                                      //创建面板对象
        jFrame.add(game);                                                    //将面板添加到窗口中
        jFrame.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));                //设置窗口大小
        jFrame.pack();
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //设置窗口默认关闭操作(关闭窗口时退出程序)
        jFrame.setLocationRelativeTo(null);                    //设置居中显示
        jFrame.setVisible(true);                               //1)设置窗口可见  2)尽快调用paint()方法

        backgroundMusic.loop();
        game.action();                                         //启动程序的执行
    }


    /**
     * 生成面包对象
     */
    public Movable generateBread() {
        return new DropBread();
    }

    public void action() {
        //创建鼠标侦听器
        MouseAdapter l = new MouseAdapter() {
            /** 重写mouseMoved()鼠标移动事件 */
            public void mouseMoved(MouseEvent e) {
                if (state == RUNNING) {                      //运行状态下执行
                    int x = e.getX();                        //获取鼠标的x坐标
                    int y = e.getY();                        //获取鼠标的y坐标
                    wang.moveTo(x, y);                       //王思聪随着鼠标移动
                }
            }

            /** 重写mouseClicked()鼠标点击事件 */
            public void mouseClicked(MouseEvent e) {
                switch (state) {                                //根据当前状态做不同的操作
                    case START:                                 //启动状态时
                        state = RUNNING;                        //修改为运行状态
                        introMusic.play();
                        break;
                    case GAME_OVER:                             //游戏结束状态时
                        score = 0;                              //清理现场
                        wang = new Wang();
                        droppingBread = new Movable[0];
                        state = START;                          //修改为启动状态
                        break;
                }
            }

            /** 重写mouseExited()鼠标移出事件 */
            public void mouseExited(MouseEvent e) {
                if (state == RUNNING) {                       //运行状态时
                    state = PAUSE;                            //修改为暂停状态
                }
            }

            /** 重写mouseEntered()鼠标移入事件 */
            public void mouseEntered(MouseEvent e) {
                if (state == PAUSE) {                           //暂停状态时
                    state = RUNNING;                            //修改为运行状态
                }
            }
        };
        this.addMouseListener(l);                          //处理鼠标操作事件
        this.addMouseMotionListener(l);                    //处理鼠标滑动事件

        Timer timer = new Timer();                            //创建定时器对象
        int intervel = 10;                                    //时间间隔(以毫秒为单位)
        timer.schedule(new TimerTask() {
            public void run() {                                 //定时干的那个事--10毫秒走一次
                if (state == RUNNING) {                       //运行状态时执行
                    enterAction();                            //面包入场
                    stepAction();                             //飞行物面包、王思聪走一步
                    outOfBoundsAction();                      //删除越界的飞行物(面包)
                    hitAction();                              //王思聪与面包的碰撞
                    checkGameOverAction();                    //判断游戏结束
                }
                repaint();                                    //重画(调用paint()方法)
            }
        }, intervel, intervel);                               //定时计划
    }

    public void paint(Graphics g) {
        g.drawImage(imgBackground, 0, 0, null);            //画背景图
        paintScoreAndLives(g);                                              //画分和画命
        paintWang(g);                                                       //画王思聪对象
        paintBread(g);                                                      //画面包对象
        paintHeadingBread(g);                                               //画面包0
        paintHeadingWang(g);                                                //画王思聪0
        paintState(g);                                                      //画状态
    }

    /**
     * 画王思聪对象
     */
    public void paintWang(Graphics g) {
        g.drawImage(wang.image, wang.x, wang.y, null);    //画王思聪对象
    }

    /**
     * 画王思聪0对象
     */
    public void paintHeadingWang(Graphics g) {
        g.drawImage(headingWang.image, headingWang.x, headingWang.y, null);    //画王思聪0对象
    }

    /**
     * 画面包0对象
     */
    public void paintHeadingBread(Graphics g) {
        g.drawImage(headingBread.image, headingBread.x, headingBread.y, null);    //画王思聪对象
    }

    /**
     * 画面包对象
     */
    public void paintBread(Graphics g) {
        for (Movable f : droppingBread) {                          //遍历所有面包
            g.drawImage(f.image, f.x, f.y, null);                     //画面包
        }
    }

    /**
     * 画分和画命
     */
    public void paintScoreAndLives(Graphics g) {
        g.setColor(new Color(0x000000));                          //设置颜色
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));      //设置字体样式(字体:SANS_SERIF 样式:BOLD加粗  字号:24)
        g.drawString(" X " + score, 80, 85);
        g.drawString(" X " + wang.getLives(), 280, 85);
    }

    /**
     * 画状态
     */
    public void paintState(Graphics g) {
        switch (state) {                                           //根据当前状态的不同画不同的图
            case START:                                            //启动状态时画启动图
                g.drawImage(imgStart, 0, 0, null);
                break;
            case PAUSE:                                            //暂停状态时画暂停图
                g.drawImage(imgPause, 0, 0, null);
                break;
            case GAME_OVER:                                        //游戏结束状态时画游戏结束图
                g.drawImage(imgGameOver, 0, 0, null);
                break;
        }
    }

    int flyIndex = 0; //面包入场计数

    /**
     * 面包 =入场
     */
    public void enterAction() {
        flyIndex++;
        if (flyIndex % 80 == 0) {
            Movable obj = generateBread();                                             //获取面包对象
            //Consider ArrayList<>
            droppingBread = Arrays.copyOf(droppingBread, droppingBread.length + 1);    //扩容
            droppingBread[droppingBread.length - 1] = obj;                             //将面包添加到最后一个元素上
        }
    }

    /**
     * 飞行物(面包+王思聪)走一步
     */
    public void stepAction() {
        wang.step();                                                            //王思聪走一步
        for (Movable movable : droppingBread) {                                 //遍历所有面包
            movable.step();                                                     //面包走一步
        }
    }

    /**
     * 删除越界的飞行物面包
     */
    public void outOfBoundsAction() {
        int index = 0;                                                     //1)不越界面包数组下标  2)不越界面包个数
        Movable[] visibleBread = new Movable[droppingBread.length];        //不越界面包数组
        for (Movable f : droppingBread) {                   //遍历所有面包
            if (!f.outOfBounds()) {                                        //不越界
                visibleBread[index] = f;                                   //将不越界面包对象添加到不越界面包数组中
                index++;                                                   //1)不越界面包数组下标增一  2)不越界面包个数增一
            } else {
                wang.loseLife();
            }
        }
        droppingBread = Arrays.copyOf(visibleBread, index);

    }

    /**
     * 王思聪与面包的碰撞
     */
    public void hitAction() {
        for (int i = 0; i < droppingBread.length; i++) {                         //遍历所有面包
            Movable f = droppingBread[i];                                        //获取每一个面包
            if (wang.hit(f)) {                                                   //撞上了
                //交换被撞的面包与数组中的最后一个元素
                Movable t = droppingBread[i];
                droppingBread[i] = droppingBread[droppingBread.length - 1];
                droppingBread[droppingBread.length - 1] = t;
                //缩容(去掉最后一个元素，即被撞的面包对象)
                droppingBread = Arrays.copyOf(droppingBread, droppingBread.length - 1);

                score += 1;
            }
        }
    }

    /**
     * 判断游戏结束
     */
    public void checkGameOverAction() {
        if (wang.getLives() <= 0) {                                                   //游戏结束了
            state = GAME_OVER;                                                        //修改当前状态为游戏结束状态
        }
    }


}

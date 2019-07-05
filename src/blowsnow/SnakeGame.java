package blowsnow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 贪吃蛇
 * 
 * @description SnakeGame
 * @author westdoor_blowsnow
 * @time 2019年7月4日上午6:27:35
 *
 */
public class SnakeGame extends JPanel {
	private static final long serialVersionUID = 1903491051573654926L;

	private static final Color BACKGROUD = new Color(50, 50, 50);
//	private static final Color SNAKE_BORDER = Color.BLACK;
	private static final Color SNAKE_HEAD = new Color(0, 100, 200);
	private static final Color SNAKE_BODY = new Color(50, 188, 255);
	private static final Color SNAKE_TAIL = new Color(0, 100, 200);
	private static final Color SNAKE_EYE = new Color(255, 255, 0);
	private static final Color SNAKE_EYE_BORDER = new Color(255, 255, 255);
	private static final Color FOOD = new Color(255, 255, 0);

	private static final int CELL_SIZE = 30;

	private static final int UP = 1423;
	private static final int DOWN = 4359;
	private static final int LEFT = 78452;
	private static final int RIGHT = 2354;

	private static final int MIN_WIDTH = 5;
	private static final int MIN_HEIGHT = 5;

	private static final int GO = 3345;
	private static final int EAT = 452;
	private static final int DIE = 9347;

//	private static final double INTERVAL_MIN = 200.0;
	private static final double INTERVAL_START = 1000.0;

	private int width;
	private int height;
	private int X[];
	private int Y[];
	private int length;
	private int foodX;
	private int foodY;
	private int direction;
	private double lastInterval;

	boolean gameOver = false;
	boolean gaming = true;

	Random r;

	public static void main(String[] args) {
		SnakeGame snakeGame = new SnakeGame(25, 16);
		snakeGame.start();
	}

	public SnakeGame(int width, int height) {
		super();
		this.width = width > MIN_WIDTH ? width : MIN_WIDTH;
		this.height = height > MIN_HEIGHT ? height : MIN_HEIGHT;
		X = new int[this.width * this.height];
		Y = new int[this.width * this.height];
		X[0] = 3;
		Y[0] = 3;
		X[1] = 2;
		Y[1] = 3;
		length = 2;
		direction = RIGHT;
		lastInterval = INTERVAL_START;
		r = new Random(System.currentTimeMillis());
		nextFood();
	}

	void start() {
		URL url = this.getClass().getResource("20190703.PNG");// 文件的相对路径
		ImageIcon imageIcon = new ImageIcon(url);
		JFrame jFrame = new JFrame("Snake2.2");
		jFrame.setIconImage(imageIcon.getImage());
		jFrame.setSize(width * CELL_SIZE + 6, height * CELL_SIZE + 50);

		Scanner scanner = null;
		File file = null;
		int longestSnake = 2;
		try {
			file = new File("snake2.2.dat");
			if (file.exists()) {
				scanner = new Scanner(file, "UTF-8");
				longestSnake = scanner.nextInt();
			}
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

		JLabel jLabel = new JLabel("Snake当前长度为：" + length);
		JLabel jLabel2 = new JLabel("Snake历史最长为：" + longestSnake);
		Box box = Box.createHorizontalBox();
		box.add(Box.createGlue());
		box.add(jLabel);
		box.add(Box.createGlue());
		box.add(jLabel2);
		box.add(Box.createGlue());
		jFrame.add(box, BorderLayout.NORTH);

		this.setPreferredSize(new Dimension(width * CELL_SIZE, height * CELL_SIZE));
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (!gameOver) {
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						gaming = !gaming;
					} else if ((X[0] - X[1]) != 0) {
						if (e.getKeyCode() == KeyEvent.VK_UP) {
							direction = UP;
							repaint();
						} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
							direction = DOWN;
							repaint();
						}
					} else {
						if (e.getKeyCode() == KeyEvent.VK_LEFT) {
							direction = LEFT;
							repaint();
						} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
							direction = RIGHT;
							repaint();
						}
					}
				}
			}
		});
		setFocusable(true);
		setBackground(BACKGROUD);

		jFrame.add(this);
		jFrame.setResizable(false);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
		jFrame.setVisible(true);
		int llll = 0;
		while (!gameOver) {
			if (gaming) {
				int goOrEatOrDie = goOrEatOrDie();
				if (goOrEatOrDie == GO) {
					go();
				} else if (goOrEatOrDie == EAT) {
					eat();
					jLabel.setText("Snake当前长度为：" + length);
				} else {
					gameOver = true;
				}
				repaint();
			} else {
				System.out.println(llll++);
			}
			try {
				Thread.sleep((long) lastInterval);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		if (length > longestSnake) {
			box.remove(2);
			jLabel.setText("恭喜你，新纪录！");
			jLabel2.setText(" snake长度：" + length);
			OutputStreamWriter writer = null;
			try {
				writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
				writer.write(length + "\n");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} else {
			jLabel.setText("游戏结束！snake长度：" + length);
		}
	}

	double nextInterval() {
		if (length <= 2) {
			return lastInterval;
		}
		lastInterval = lastInterval - 25 * Math.pow(0.96758, length - 3);
		return lastInterval;
	}

	boolean nextFood() {
		int num = r.nextInt(width * height - length) + 1;
		boolean t = true;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				for (int g = 0; g < length; g++) {
					if (cellEquals(X[g], Y[g], i, j)) {
						t = false;
						break;
					}
				}
				if (t) {
					num--;
					if (num == 0) {
						foodX = i;
						foodY = j;
						return true;
					}
				} else {
					t = true;
				}
			}
		}
		return false;
	}

	void go() {
		for (int i = length - 1; i > 0; i--) {
			X[i] = X[i - 1];
			Y[i] = Y[i - 1];
		}
		switch (direction) {
		case UP:
			Y[0]--;
			break;
		case DOWN:
			Y[0]++;
			break;
		case LEFT:
			X[0]--;
			break;
		case RIGHT:
			X[0]++;
			break;
		}
	}

	void eat() {
		X[length] = X[length - 1];
		Y[length] = Y[length - 1];
		go();
		length++;
		nextFood();
		nextInterval();
	}

	int goOrEatOrDie() {
		int x = X[0];
		int y = Y[0];
		switch (direction) {
		case UP:
			y--;
			break;
		case DOWN:
			y++;
			break;
		case LEFT:
			x--;
			break;
		case RIGHT:
			x++;
			break;
		}
		if (cellEquals(x, y, foodX, foodY)) {
			return EAT;
		}
		for (int i = 3; i < length - 1; i++) {
			if (cellEquals(X[i], Y[i], x, y)) {
				return DIE;
			}
		}
		return GO;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintFood(g, foodX, foodY);
		paintHead(g, castX(X[0]), castY(Y[0]));
		paintTail(g, castX(X[length - 1]), castY(Y[length - 1]));
		for (int i = 1; i < length - 1; i++) {
			paintBody(g, castX(X[i]), castY(Y[i]));
		}
	}

	private void drawEye(Graphics g, int x, int y, int size) {
		g.setColor(SNAKE_EYE);
		g.fillOval(x, y, size, size);
		g.setColor(SNAKE_EYE_BORDER);
		g.drawOval(x, y, size, size);
	}

	private void paintHead(Graphics g, int x, int y) {
		g.setColor(SNAKE_HEAD);
		g.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
//		g.setColor(SNAKE_BORDER);
//		g.drawRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
		if (direction == RIGHT || direction == UP) {
			drawEye(g, x * CELL_SIZE + CELL_SIZE / 3 * 2, y * CELL_SIZE, CELL_SIZE / 5 * 2);
		}
		if (direction == RIGHT || direction == DOWN) {
			drawEye(g, x * CELL_SIZE + CELL_SIZE / 3 * 2, y * CELL_SIZE + CELL_SIZE / 3 * 2, CELL_SIZE / 5 * 2);
		}
		if (direction == LEFT || direction == DOWN) {
			drawEye(g, x * CELL_SIZE, y * CELL_SIZE + CELL_SIZE / 3 * 2, CELL_SIZE / 5 * 2);
		}
		if (direction == LEFT || direction == UP) {
			drawEye(g, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE / 5 * 2);
		}
	}

	private void paintBody(Graphics g, int x, int y) {
		g.setColor(SNAKE_BODY);
		g.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
//		g.setColor(SNAKE_BORDER);
//		g.drawRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
	}

	private void paintTail(Graphics g, int x, int y) {
		g.setColor(SNAKE_TAIL);
		g.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
//		g.setColor(SNAKE_BORDER);
//		g.drawRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
	}

	private void paintFood(Graphics g, int x, int y) {
		g.setColor(FOOD);
		g.fillOval(x * CELL_SIZE + CELL_SIZE / 6, y * CELL_SIZE + CELL_SIZE / 6, CELL_SIZE / 3 * 2, CELL_SIZE / 3 * 2);
	}

	private boolean cellEquals(int x, int y, int x1, int y1) {
		return castX(x) == castX(x1) && castY(y) == castY(y1);
	}

	private int castX(int x) {
		return (x % width + width) % width;
	}

	private int castY(int y) {
		return (y % height + height) % height;
	}
}

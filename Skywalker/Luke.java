package Skywalker;
import robocode.*;
import robocode.util.*;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.io.*;

/**
 * Luke - a robot by (Enrique e Marcos)
 */
public class Luke extends AdvancedRobot {
	private boolean movingForward;
	private boolean inWall;
	private byte radarDirection = 1;
	private float nearWall = 100;
	private float aim = 2;
	private int scanReset = 0;
	private int scanLimit = 5;
	private int shootReset = 0;
	private int shootLimit = 3;
	private ArrayList dados;
	private String fileName = "dados.dat";

	private EnemyBot enemy = new EnemyBot();

	public void run() {
		dados = new ArrayList();

		// ===================
		this.salvarDados();
		this.lerDados();
		
		// ===================
		this.inWall = checkWall();
		this.enemy.reset();
		this.movingForward = true;
		
		setRoboColors();
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAhead(40000);
		setTurnRadarRight(360);
		
		while(true) {		
			if(checkWall() && !this.inWall) {
				reverseDirection();
			}
			this.inWall = checkWall();

			if (getRadarTurnRemaining() <= 0) {
				setTurnRadarRight(360);	
			}			

			shoot();
			scan();
			execute();
		}
	}
	
	public boolean checkWall() {
		return (getX() < nearWall || getY() < nearWall
			|| getBattleFieldWidth() - getX() < nearWall
			|| getBattleFieldHeight() - getY() < nearWall
		);
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		// double absoluteBearing = getHeading() + e.getBearing();
		// double bearingFromGun = Utils.normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
		// double bearingFromRadar = Utils.normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
		
		if (this.movingForward) {
			setTurnRight(Utils.normalRelativeAngleDegrees(e.getBearing() + 80));			
		} else {
			setTurnRight(Utils.normalRelativeAngleDegrees(e.getBearing() + 100));
		}
		
		if(enemy.none() || e.getName() == enemy.getName()) {
			enemy.update(e, this);
			this.scanReset = 0;
			if (enemy.getSwitchDirection()) {
				reverseDirection();
			}
		}
	}

	public void shoot() {
		if (enemy.none()) {
			return;
		}

		double firePower = Math.min(500 / enemy.getDistance(), 3);
		double bulletSpeed = 20 - firePower * 3;
		long time = (long)(enemy.getDistance() / bulletSpeed);

		double nextX = enemy.getFutureX(time);
		double nextY = enemy.getFutureY(time);
		double absDeg = absoluteBearing(getX(), getY(), nextX, nextY);
		setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));
		
		if (getGunHeat() <= 0) {
			this.shootReset++;
			System.out.println(this.shootReset);
			if ((Math.abs(getGunTurnRemaining()) <= this.aim) || this.shootReset > this.shootLimit) {
				this.shootReset = 0;
				setFire(firePower);
				System.out.println("Set");
			}
		}
	}

	public void scan() {
		if (this.scanReset > this.scanLimit || enemy.none()) {
			setTurnRadarRight(36000);
		} else {
			double turn = getHeading() - getRadarHeading() + enemy.getBearing();
			turn += 30 * radarDirection;
			setTurnRadarRight(turn);
			radarDirection *= -1;
			this.scanReset++;
		}
	}
	
	public void reverseDirection() {
		if (this.movingForward) {
			setBack(40000);
			this.movingForward = false;
		} else {
			setAhead(40000);
			this.movingForward = true;
		}
	}
	
	private void setRoboColors() {
		setBodyColor(Color.RED);
		setGunColor(Color.WHITE);
		setRadarColor(Color.BLACK);
		setBulletColor(Color.GREEN);
		setScanColor(Color.RED);
	}

	public void onHitByBullet(HitByBulletEvent e) {
		reverseDirection();
		this.enemy.bulletHit(e.getPower(), this);
	}
	
	public void onHitWall(HitWallEvent e) {
		reverseDirection();
	}
	
	public void onHitRobot(HitRobotEvent e) {
		if(e.isMyFault()) {
			reverseDirection();
		}
	}
	
	public void onRobotDeath(RobotDeathEvent e) {
		// enemy.printBullets();
		compileDados();
		if (e.getName().equals(enemy.getName())) {
			enemy.reset();
		}
	}
	
	public void onDeath(DeathEvent e) {
		// enemy.printBullets();
		compileDados();
	}
	
	// TESTE DE DADOS
	private void compileDados() {
		for (int i = 0; i < this.enemy.getHistoricoSize(); i++) {
			this.dados.add(this.enemy.getData(false, i));
		}
		
		for (int i = 0; i < this.enemy.getHistoricoTomadaSize(); i++) {
			this.dados.add(this.enemy.getData(true, i));	
		}
		writeDados();
	}
	
	private void writeDados() {
		System.out.println("END RESUTS");
		for (int i = 0; i < this.dados.size(); i++) {
			double[] aux = (double[])this.dados.get(i);
        	for (int j = 0; j < aux.length; j++) {
				System.out.println((j + 1) + " - " + aux[j]);
			}
        }
	}
	

	// UTILIDADES
	double normalizeBearing(double angle) {
		while (angle >  180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}
	
	double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2-x1;
		double yo = y2-y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;

		if (xo > 0 && yo > 0) {
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { 
			bearing = 360 + arcSin; 
		} else if (xo > 0 && yo < 0) { 
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) {
			bearing = 180 - arcSin; 
		}
		
		return bearing;
	}
	
	void salvarDados() {
		double roundCount = 0.1, battleCount = 0.99;
		System.out.println(this.getDataDirectory());
		PrintStream w = null;
		try {
			w = new PrintStream(new RobocodeFileOutputStream(getDataFile(this.fileName)));

			// Escreve dados
			w.println(roundCount);
			w.println(battleCount);

			if (w.checkError()) {
				System.out.println("checkError");
			} else {
				System.out.println("Saved");
			}
		} catch (IOException e) {
			System.out.println("Erro IOException");
			e.printStackTrace(out);
		} finally {
			if (w != null) {
				w.close();
			}
		}
	}
	
	void lerDados() {
		double roundCount, battleCount;
		try {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(getDataFile(this.fileName)));
				
				// Lê dados
				roundCount = Double.parseDouble(reader.readLine());
				battleCount = Double.parseDouble(reader.readLine());
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (IOException e) {
			// Hard set em caso de erros
			System.out.println("Erro IOException");
			roundCount = 0;
			battleCount = 0;
		} catch (NumberFormatException e) {
			// Hard set em caso de erros
			System.out.println("Erro NumberFormatException");
			roundCount = 0;
			battleCount = 0;
		}
		System.out.println(roundCount);
		System.out.println(battleCount);
	}
}

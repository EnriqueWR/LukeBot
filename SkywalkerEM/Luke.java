package SkywalkerEM;
import robocode.*;
import robocode.util.*;
//import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.awt.Color;
//import java.io.*;

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
	// private ArrayList dados;
	// private String fileName = "dados.dat";

	private EnemyBot enemy = new EnemyBot();
	private LukeMente mente = new LukeMente();
	// private int pesosCount = 9;

	public void run() {
		// System.out.println(this.getDataDirectory());
		
		this.enemy.setMente(this.mente);
		this.inWall = checkWall();
		this.enemy.reset();
		this.movingForward = true;
		
		//System.out.println(getNumRounds());
		if (getRoundNum() > 4) {
			setRoboColors2();
		} else {
			setRoboColors();
		}
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
	
	private void setRoboColors() {
		setBodyColor(Color.WHITE);
		setGunColor(Color.BLUE);
		setRadarColor(Color.WHITE);
		setBulletColor(Color.BLUE);
		setScanColor(Color.WHITE);
	}

	private void setRoboColors2() {
		setBodyColor(Color.BLACK);
		setGunColor(Color.GREEN);
		setRadarColor(Color.BLACK);
		setBulletColor(Color.GREEN);
		setScanColor(Color.BLACK);
	}

	public boolean checkWall() {
		return (getX() < nearWall || getY() < nearWall
			|| getBattleFieldWidth() - getX() < nearWall
			|| getBattleFieldHeight() - getY() < nearWall
		);
	}

	public void onScannedRobot(ScannedRobotEvent e) {	
		if (this.movingForward) {
			setTurnRight(Utils.normalRelativeAngleDegrees(e.getBearing() + 80));			
		} else {
			setTurnRight(Utils.normalRelativeAngleDegrees(e.getBearing() + 100));
		}
		
		if(enemy.none() || e.getName() == enemy.getName()) {
			enemy.update(e, this);
			this.scanReset = 0;
			if (enemy.switchAux()) {
				SkywalkerEM.EnemyBot.Bullet bullet = enemy.getSwitchDirection(this.getX(), this.getY());
				
				System.out.println("Dados de input:\n");
				System.out.println("Distancia: " + bullet.distancia2player);
				System.out.println("Velocidade: " + bullet.velocity);
				System.out.println("Direcao:  " + bullet.direction);
				System.out.println("Angulo: " + bullet.directAngle);
				
				System.out.println("Resposta? " + (bullet.valueActivation > 0.5 ? "Sim " : "Nao ") + bullet.valueActivation);
				if (bullet.valueActivation > 0.5) {
					reverseDirection();
				}
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
			if ((Math.abs(getGunTurnRemaining()) <= this.aim) || this.shootReset > this.shootLimit) {
				this.shootReset = 0;
				setFire(firePower);
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

	public void onHitByBullet(HitByBulletEvent e) {
		this.enemy.bulletHit(e, this);
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
		// salvarDados();
		if (e.getName().equals(enemy.getName())) {
			enemy.reset();
		}
	}
	
	public void onDeath(DeathEvent e) {
		// enemy.printBullets();
		// compileDados();
		// salvarDados();
	}
	
	// TESTE DE DADOS
	//private void compileDados() {
		//this.dados = new ArrayList();
		// Chama aprendizado aqui
		//for (int i = 0; i < this.enemy.getHistoricoSize(); i++) {
			//this.dados.add(this.enemy.getData(false, i));
		//}
		
		//for (int i = 0; i < this.enemy.getHistoricoTomadaSize(); i++) {
			//this.dados.add(this.enemy.getData(true, i));	
		//}
		// writeDados();
		// salvarDados();
	//}
	
	//private void writeDados() {
		//System.out.println("END RESUTS");
		//for (int i = 0; i < this.dados.size(); i++) {
		//	double[] aux = (double[])this.dados.get(i);
        //	for (int j = 0; j < aux.length; j++) {
		//		System.out.println((j + 1) + " - " + aux[j]);
		//	}
        //}
	//}

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
	
	//void salvarDados() {
		//System.out.println("Sem treinamento");
		/*	
		int roundsTrained = this.enemy.callTraining();
		ArrayList listaPesos = mente.getPesos();
		System.out.println(this.getDataDirectory());
		System.out.println("Treinou por " + roundsTrained + " rounds!");
		System.out.println("Balas evitadas: " + this.enemy.getHistoricoSize());
		System.out.println("Balas tomadas: " + this.enemy.getHistoricoTomadaSize());
		
		PrintStream w = null;
		try {
			w = new PrintStream(new RobocodeFileOutputStream(getDataFile(this.fileName)));

			// Escreve dados
			for (int i = 0; i < listaPesos.size(); i++) {
				w.println(listaPesos.get(i));				
			}

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
		*/
	//}
	
	//void lerDados() {
		//System.out.println("Dados estao em LukeMente");
        /*
		ArrayList listaPesos = new ArrayList();
		try {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(getDataFile(this.fileName)));
				
				// Lê dados
				for (int i = 0; i < this.pesosCount; i++) {
					listaPesos.add(Double.valueOf(reader.readLine().toString())); // n funciona
				}
				
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (IOException e) {
			// Hard set em caso de erros
			System.out.println("Erro IOException");
		} catch (NumberFormatException e) {
			// Hard set em caso de erros
			System.out.println("Erro NumberFormatException");
		}
		
		this.mente = new LukeMente(listaPesos);
        */
	//}
}

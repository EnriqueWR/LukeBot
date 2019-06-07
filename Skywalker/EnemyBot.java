package Skywalker;
import robocode.*;
import robocode.ScannedRobotEvent;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;
import java.util.ArrayList;
import java.io.*;

public class EnemyBot extends AdvancedRobot {

	private double constVelocity = 13;
	private double constDistance = 1;
	private double constAngle = 3;

	private volatile double bearing;
	private volatile double distance;
	private volatile double energy;
	private volatile double oldEnergy;
	private volatile double heading;
	private volatile String name = "";
	private volatile double velocity;
	private volatile double x;
	private volatile double y;
	private volatile int lateralVelocity;
	private volatile double absBearing;
	private volatile int balasCount;
	public volatile LinkedList listaBalas;
	public volatile LinkedList listaBalasHistorico;
	public volatile LinkedList listaBalasHistoricoTomada;
	private volatile Random gerador;

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	public double getEnergy() {
		return energy;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public double getHeading() {
		return heading;
	}

	public void setHeading(double heading) {
		this.heading = heading;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public void reset() {
		this.bearing = 0.0;
		this.distance = 0.0;
		this.energy = 0.0;
		this.oldEnergy = 0.0;
		this.heading = 0.0;
		this.name = "";
		this.velocity = 0.0;
		this.x = 0.0;
		this.y = 0.0;
		this.lateralVelocity = 1;
		this.absBearing = 0.0;
		this.balasCount = 1;
		this.listaBalas = new LinkedList();
		this.listaBalasHistorico = new LinkedList();
		this.listaBalasHistoricoTomada = new LinkedList();
		this.gerador = new Random();
	}

	public void update(ScannedRobotEvent e, AdvancedRobot robot) {
		this.bearing = e.getBearing();
		this.distance = e.getDistance();
		this.energy = e.getEnergy();
		this.heading = e.getHeading();
		this.name = e.getName();
		this.velocity = e.getVelocity();
		
		double bulletPower = this.oldEnergy - this.energy;
		this.oldEnergy = this.energy;
		
		if (bulletPower <= 3.0 && bulletPower >= 0.1) {
			Bullet bullet = new Bullet(
				new Point2D.Double(x, y),
				robot.getTime() - 1,
				bulletPower,
				this.lateralVelocity,
				this.absBearing,
				this.balasCount,
				this.gerador.nextInt(2) > 0
			);
			// bullet.details();
			this.balasCount++;
			this.listaBalas.addLast(bullet);
		}
		updateBullets(robot);

		this.lateralVelocity = (robot.getVelocity() * Math.sin(e.getBearingRadians())) >= 0 ? 1 : -1;
		
		double absBearingTemp = e.getBearingRadians() + robot.getHeadingRadians();
		this.absBearing = absBearingTemp + Math.PI;
		this.x = robot.getX() + Math.sin(absBearingTemp) * e.getDistance();
		this.y = robot.getY() + Math.cos(absBearingTemp) * e.getDistance();
	}

	public boolean none() {
		return "".equals(name);
	}

	public double getFutureX(long when){
		return x + Math.sin(Math.toRadians(getHeading())) * getVelocity() * when;
	}

	public double getFutureY(long when){
		return y + Math.cos(Math.toRadians(getHeading())) * getVelocity() * when;
	}

	// ====== CONTROLE DE BALAS ====== //
	public double bulletVelocity(double power) {
		return (20.0 - (3.0 * power));
	}
	
	public Point2D.Double getBulletLocation() {
		Bullet bullet = (Bullet) listaBalas.getFirst();
		return bullet.location;
	}
	
	public int getBulletDirection() {
		Bullet bullet = (Bullet) listaBalas.getFirst();
		return bullet.direction;
	}
	
	public double getBulletVelocity() {
		Bullet bullet = (Bullet) listaBalas.getFirst();
		return bullet.velocity;
	}
	
	public double getBulletDistanceTraveled() {
		Bullet bullet = (Bullet) listaBalas.getFirst();
		return bullet.distanceTraveled;
	}
	
	public double getBulletDirectAngle() {
		Bullet bullet = (Bullet) listaBalas.getFirst();
		return bullet.directAngle;
	}
	
	public boolean getSwitchDirection() {
		if (listaBalas.size() > 0) {
			Bullet bullet = (Bullet) listaBalas.getFirst();
			return bullet.switchDirection;
		}
		return false;
	}

	public void updateBullets(AdvancedRobot robot) {
		Point2D.Double location = new Point2D.Double(robot.getX(), robot.getY());

		for (int i = 0; i < listaBalas.size(); i++) {
			Bullet bullet = (Bullet)listaBalas.get(i);
			bullet.distanceTraveled = (robot.getTime() - bullet.time) * bullet.velocity;
			if (bullet.distanceTraveled > location.distance(bullet.location) + 50) {
				this.listaBalasHistorico.addLast(bullet);
				listaBalas.remove(i);
				i--;
			}
		}
	}
	
	public void bulletHit(double bulletPower, AdvancedRobot robot) {
		for (int i = 0; i < listaBalas.size(); i++) {
			Bullet bullet = (Bullet)listaBalas.get(i);
			bullet.distanceTraveled = (robot.getTime() - bullet.time) * bullet.velocity;
			if (bullet.power == bulletPower) {
				this.listaBalasHistoricoTomada.addLast(bullet);
				listaBalas.remove(i);
				break;
			}
		}
		/* BALAS NAO DESCOBERTAS POREM TOMADAS
		Point2D.Double location = new Point2D.Double(robot.getX(), robot.getY());
		Bullet bullet = new Bullet(location, 0, bulletPower, 0, 0, this.balasCount);
		this.balasCount++;
		this.listaBalasHistoricoTomada.addLast(bullet);
		*/
	}
	
	public void printBullets() {
		System.out.println("BALAS HISTORICO");
		System.out.println("\nNO AR\n");
		for (int i = 0; i < this.listaBalas.size(); i++) {
			Bullet bullet = (Bullet)listaBalas.get(i);
			bullet.details();
		}
		
		System.out.println("\nESQUIVADAS\n");
		for (int i = 0; i < this.listaBalasHistorico.size(); i++) {
			Bullet bullet = (Bullet)listaBalasHistorico.get(i);
			bullet.details();
		}
		
		System.out.println("\nTOMADAS\n");
		for (int i = 0; i < this.listaBalasHistoricoTomada.size(); i++) {
			Bullet bullet = (Bullet)listaBalasHistoricoTomada.get(i);
			bullet.details();
		}
	}
	
	public int getHistoricoSize() {
		return this.listaBalasHistorico.size();
	}
	
	public int getHistoricoTomadaSize() {
		return this.listaBalasHistoricoTomada.size();
	}

	public double[] getData(boolean tomada, int index) {
		Bullet bullet = tomada ?
							(Bullet)listaBalasHistoricoTomada.get(index) :
							(Bullet)listaBalasHistorico.get(index);
		
		double[] aux = {
			1 / (1 + Math.exp((bullet.velocity - constVelocity) * -1)),
			1 / (1 + Math.exp(((bullet.distanceTraveled / 100) - constDistance) * -1)),
			bullet.direction,
			1 / (1 + Math.exp((bullet.directAngle - constAngle) * -1)),
			bullet.switchDirection ? 1.0 : 0.0,
			tomada ? 0.0 : 1.0
		};
		
		/*
		double[] aux = {
			bullet.velocity,
			bullet.distanceTraveled,
			bullet.direction,
			bullet.directAngle,
			bullet.switchDirection ? 1.0 : 0.0,
			tomada ? 0.0 : 1.0
		};		
		*/
		return aux;
	}
	
	class Bullet implements Serializable{
		Point2D.Double location;
		int number;
		long time;
		double power;
		double velocity;
		double distanceTraveled;
		int direction;
		double directAngle;
		boolean switchDirection;
		
		public void details() {
			System.out.println("BULLET N" + this.number + " - " + (this.switchDirection ? "SIM" : "NAO") + ":\n");
			System.out.println("Location: " + this.location.x + " " + this.location.y);
			System.out.println("Tempo: " + this.time);
			System.out.println("Forca: " + this.power);
			System.out.println("Velocidade: " + this.velocity);
			System.out.println("Distancia: " + this.distanceTraveled);
			System.out.println("Direcao: " + this.direction);
			System.out.println("Angulo: " + this.directAngle);
			System.out.println("-------------------------------------------\n");
		}
		
		public Bullet(Point2D.Double location, long time, double power, int direction, double directAngle, int number, boolean switchDirection) {
			this.number = number;
			this.location = location;
			this.time = time;
			this.power = power;
			this.velocity = bulletVelocity(power);
			this.distanceTraveled = bulletVelocity(power);
			this.direction = direction;
			this.directAngle = directAngle;
			this.switchDirection = switchDirection;
		}
	}
}

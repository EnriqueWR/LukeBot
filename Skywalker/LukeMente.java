package Skywalker;
import java.util.ArrayList;
import java.util.Random;


/**
 * Feito por Enrique Wicks
 * 
 * Fontes de estudo:
 * 
 * João Paulo - Slides sobre RNA
 * Victor Zhou - https://towardsdatascience.com/machine-learning-for-beginners-an-introduction-to-neural-networks-d49f22d238f9
 * 
 **/


public class LukeMente {
	ArrayList neuronios;
	ArrayList dados;
	double learningRate = 0.01;
	double respTeste;
	
	public static void main(String[] args) {
		Random r = new Random();
		
		ArrayList dados = new ArrayList();
		/*
		for (int i = 0; i < 9; i++) {
			dados.add(r.nextDouble());
		}
		*/
		
		dados.add(1.0);
		dados.add(1.0);
		dados.add(1.0);
		
		dados.add(0.0);
		dados.add(1.0);
		dados.add(1.0);
		
		dados.add(0.0);
		dados.add(1.0);
		dados.add(1.0);
		
		
		LukeMente mente = new LukeMente(dados);
		
		for (int i = 0; i < 1000000; i++) {
			mente.respTeste = 1.0;
			double resposta = mente.activate(-2, -1);
			
			if (i % 10 == 0) {
				System.out.println("Perda Alice: " + mente.getPerda(mente.respTeste, resposta));
				System.out.println(resposta);
			}
			
			mente.respTeste = 0.0;
			resposta = mente.activate(25, 6);
			
			if (i % 10 == 0) {
				System.out.println("Perda Bob: " + mente.getPerda(mente.respTeste, resposta));
				System.out.println(resposta);
			}
			
			mente.respTeste = 0.0;
			resposta = mente.activate(17, 4);
			
			if (i % 10 == 0) {
				System.out.println("Perda Charlie: " + mente.getPerda(mente.respTeste, resposta));
				System.out.println(resposta);
			}
			
			mente.respTeste = 1.0;
			resposta = mente.activate(-15, -6);
			
			if (i % 10 == 0) {
				System.out.println("Perda Diana: " + mente.getPerda(mente.respTeste, resposta));
				System.out.println(resposta);
				if (mente.getPerda(mente.respTeste, resposta) < 0.005) {
					System.out.println("Pronto! " + i + " passagens.");
					break;
				}
			}
			
		}
		
		
	}
	
	private double getNextValue() {
		return (double) this.dados.remove(0);
	}
	
	private double[] getDadosNeuronio(int qtd) {
		double[] resp = new double[qtd];
		for (int i = 0; i < qtd; i++) {
			resp[i] = this.getNextValue();
		}
		
		return resp;
	}
	
	public LukeMente(ArrayList dados) {
		/**
		 * Pegar pesos e bias do arquivo dados.dat do robo
		 * 
		 */
		this.dados = dados;
		
		Neuronio n1 = new Neuronio(getDadosNeuronio(3));
		Neuronio n2 = new Neuronio(getDadosNeuronio(3));
		Neuronio n3 = new Neuronio(getDadosNeuronio(3));
		
		this.neuronios = new ArrayList();
		this.neuronios.add(n1);
		this.neuronios.add(n2);
		this.neuronios.add(n3);
	}
	
	public double activate(double parm1, double parm2) {
		Neuronio n1 = (Neuronio) this.neuronios.get(0);
		Neuronio n2 = (Neuronio) this.neuronios.get(1);
		Neuronio n3 = (Neuronio) this.neuronios.get(2);
		
		double[] input1 = { parm1, parm2 };
		double output1 = n1.activate(input1);
		double output2 = n2.activate(input1);
		double[] input2 = { output1, output2 }; 
		
		double output3 = n3.activate(input2);

		
		// Metodo de treinamento: Stochastic Gradient Descent
		double derivadaParcial = this.learningRate * (-2 * (this.respTeste - output3));
		
		double ajusteGeral1 = derivadaParcial * n3.pesos[0] * derivadaAprendizado(output3);
		double pesoAux1 = derivadaAprendizado(output1);
		double[] valoresPeso1 = { pesoAux1, pesoAux1 * parm1, pesoAux1 * parm2 };
		n1.treinar(ajusteGeral1, valoresPeso1);
		
		double ajusteGeral2 = derivadaParcial * n3.pesos[1] * derivadaAprendizado(output3);
		double pesoAux2 = derivadaAprendizado(output2);
		double[] valoresPeso2 = { pesoAux2, pesoAux2 * parm1, pesoAux2 * parm2 };
		n2.treinar(ajusteGeral2, valoresPeso2);
		
		double ajusteGeral3 = derivadaParcial * derivadaAprendizado(output3);
		double pesoAux3 = derivadaAprendizado(output3);
		double[] valoresPeso3 = { pesoAux3, pesoAux3 * output1, pesoAux3 * output2};
		n3.treinar(ajusteGeral3, valoresPeso3);
		
		return output3;
	}
	
	private double getPerda(double valorVerdadeiro, double valorEsperado) {
		// O valor de erro é a raiz quadrada da diferenca 
		// entre o que a rede achou e o esperado 
		return Math.pow((valorVerdadeiro - valorEsperado), 2);
	}
	
	private double derivadaAprendizado(double value) {
		return value * (1 - value);
	}
	
	
	class Neuronio {
		double[] pesos;
		double bias;
		
		public Neuronio(double[] dados) {
			if (dados.length < 2) {
				System.out.print("ERROR: Dados insuficientes: " + dados.length);
				return;
			}
			this.bias = dados[0];
			this.pesos = new double[dados.length - 1];
			for (int i = 1; i < dados.length; i++) {
				this.pesos[i - 1] = dados[i];
			}
		}
		
		private double funcaoNormal(double value) {
			// Sigmoid retorna um valor entre 0 e 1
			return 1 / (1 + Math.exp(-1 * value));
		}
		
		public void treinar(double valorGeral, double[] valoresPeso) {
			this.bias -= valorGeral * valoresPeso[0];
			for (int i = 1; i < valoresPeso.length; i++) {
				this.pesos[i - 1] -= valorGeral * valoresPeso[i];
			}
		}
		
		public double activate(double[] input) {
			if (input.length != this.pesos.length) {
				System.out.println("N# de pesos diferente de N# input:");
				System.out.println("Input: " + input.length);
				System.out.println("Pesos: " + pesos.length);
				System.out.println("Bias: " + bias);
				return 0.0;
			}
			
			double soma = bias;
			for (int i = 0; i < input.length; i++) {
				soma += input[i] * pesos[i];
			}

			return funcaoNormal(soma);
		}
	}
}

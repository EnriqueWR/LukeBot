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
	
	private double getNextValue() {
		return Double.parseDouble(this.dados.remove(0).toString());
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
		
		Neuronio n1 = new Neuronio(getDadosNeuronio(5));
		Neuronio n2 = new Neuronio(getDadosNeuronio(5));
		Neuronio n3 = new Neuronio(getDadosNeuronio(3));
		//Neuronio n4 = new Neuronio(getDadosNeuronio(3));
		//Neuronio n5 = new Neuronio(getDadosNeuronio(3));
		
		this.neuronios = new ArrayList();
		this.neuronios.add(n1);
		this.neuronios.add(n2);
		this.neuronios.add(n3);
		//this.neuronios.add(n4);
		//this.neuronios.add(n5);
	}
	
	public double activate(double[] parms) {
		Neuronio n1 = (Neuronio) this.neuronios.get(0);
		Neuronio n2 = (Neuronio) this.neuronios.get(1);
		Neuronio n3 = (Neuronio) this.neuronios.get(2);
		//Neuronio n4 = (Neuronio) this.neuronios.get(3);
		//Neuronio n5 = (Neuronio) this.neuronios.get(4);
		
		double[] input1 = { parms[0], parms[1], parms[2], parms[3],  };
		double output1 = n1.activate(input1);
		double output2 = n2.activate(input1);
		double[] input2 = { output1, output2 }; 
		double output3 = n3.activate(input2);
		return output3;
		//double output4 = n4.activate(input2);
		//double[] input3 = { output3, output4 }; 
		//double output5 = n5.activate(input3);
		
		//return output5;
	}
	
	public void train(double[] parms, double resp) {
		Neuronio n1 = (Neuronio) this.neuronios.get(0);
		Neuronio n2 = (Neuronio) this.neuronios.get(1);
		Neuronio n3 = (Neuronio) this.neuronios.get(2);
		//Neuronio n4 = (Neuronio) this.neuronios.get(3);
		//Neuronio n5 = (Neuronio) this.neuronios.get(4);
		
		double[] input1 = { parms[0], parms[1], parms[2], parms[3] };
		double output1 = n1.activate(input1);
		double output2 = n2.activate(input1);
		double[] input2 = { output1, output2 }; 
		double output3 = n3.activate(input2);
		
		double derivadaParcial = this.learningRate * (-2 * (resp - output3));
		
		// N1
		double ajusteGeral1 = derivadaParcial * n3.pesos[0] * derivadaAprendizado(output3);
		double pesoAux1 = derivadaAprendizado(output1);
		double[] valoresPeso1 = { pesoAux1, pesoAux1 * parms[0], pesoAux1 * parms[1], pesoAux1 * parms[2], pesoAux1 * parms[3] };
		n1.treinar(ajusteGeral1, valoresPeso1);
		
		// N2
		double ajusteGeral2 = derivadaParcial * n3.pesos[1] * derivadaAprendizado(output3);
		double pesoAux2 = derivadaAprendizado(output2);
		double[] valoresPeso2 = { pesoAux2, pesoAux2 * parms[0], pesoAux2 * parms[1], pesoAux2 * parms[2], pesoAux2 * parms[3] };
		n2.treinar(ajusteGeral2, valoresPeso2);

		// N3
		double ajusteGeral3 = derivadaParcial * derivadaAprendizado(output3);
		double pesoAux3 = derivadaAprendizado(output3);
		double[] valoresPeso3 = { pesoAux3, pesoAux3 * output1, pesoAux3 * output2};
		n3.treinar(ajusteGeral3, valoresPeso3);
		
		
		
		
		
		
		//double output4 = n4.activate(input2);
		//double[] input3 = { output3, output4 }; 
		//double output5 = n5.activate(input3);
		
		/*
		// Metodo de treinamento: Stochastic Gradient Descent
		double derivadaParcial = this.learningRate * (-2 * (resp - output5));
		
		// N1
		double ajusteGeral1 = derivadaParcial * n3.pesos[0] * n4.pesos[0] * derivadaAprendizado(output5);
		double pesoAux1 = derivadaAprendizado(output1);
		double[] valoresPeso1 = { pesoAux1, pesoAux1 * parms[0], pesoAux1 * parms[1], pesoAux1 * parms[2], pesoAux1 * parms[3] };
		n1.treinar(ajusteGeral1, valoresPeso1);
		
		// N2
		double ajusteGeral2 = derivadaParcial * n3.pesos[1] * n4.pesos[1] * derivadaAprendizado(output5);
		double pesoAux2 = derivadaAprendizado(output2);
		double[] valoresPeso2 = { pesoAux2, pesoAux2 * parms[0], pesoAux2 * parms[1], pesoAux2 * parms[2], pesoAux2 * parms[3] };
		n2.treinar(ajusteGeral2, valoresPeso2);

		// N3
		double ajusteGeral3 = derivadaParcial * n5.pesos[0] * derivadaAprendizado(output5);
		double pesoAux3 = derivadaAprendizado(output3);
		double[] valoresPeso3 = { pesoAux3, pesoAux3 * output1, pesoAux3 * output2};
		n3.treinar(ajusteGeral3, valoresPeso3);
		
		// N4
		double ajusteGeral4 = derivadaParcial * n5.pesos[1] * derivadaAprendizado(output5);
		double pesoAux4 = derivadaAprendizado(output4);
		double[] valoresPeso4 = { pesoAux4, pesoAux4 * output1, pesoAux4 * output2};
		n4.treinar(ajusteGeral4, valoresPeso4);
		
		// N3
		double ajusteGeral5 = derivadaParcial * derivadaAprendizado(output5);
		double pesoAux5 = derivadaAprendizado(output5);
		double[] valoresPeso5 = { pesoAux5, pesoAux5 * output3, pesoAux5 * output4};
		n5.treinar(ajusteGeral5, valoresPeso5);
		*/
	}
	
	public ArrayList getPesos() {
		ArrayList resp = new ArrayList();
		Neuronio nAux;
		for (int i = 0; i < this.neuronios.size(); i++) {
			nAux = (Neuronio) this.neuronios.get(i);
			resp.add(Double.valueOf(nAux.bias));
			System.out.println("Neuronio: " + (i + 1));
			System.out.println("Bias: " + nAux.bias);
			for (int j = 0; j < nAux.pesos.length; j++) {
				resp.add(Double.valueOf(nAux.pesos[j]));
				System.out.println("Peso " + (j + 1) + ": " + nAux.pesos[j]);
			}
		}
		
		return resp;
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

package machineLearning.neuralnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import machineLearning.neuralnetwork.activationFunction.ActivationFunction;

public class Neuron 
{
	public ArrayList<Connection> connections = new ArrayList<Connection>();
	
	public double input = 0.0;
	public double output = 0.0;
	//public double desired_output;
	public double error = 0.0;
	public double dropoutMultiplier = 1.0;
	ActivationFunction activation;
	
	public enum NeuronTypes
	{
		INPUT,
		HIDDEN,
		OUTPUT,
		BIAS
	};
	
	public NeuronTypes neuron_type;
	
	public Neuron(NeuronTypes nt, ActivationFunction activation)
	{
		neuron_type = nt;
		this.activation = activation;
//		if(neuron_type != NeuronTypes.INPUT)
//		{
//			AddConnection(new Neuron(NeuronTypes.BIAS, 1.0));
//		}
	}
	
	public Neuron(NeuronTypes nt, ActivationFunction activation, float value)
	{
		this(nt, activation);
		output = value;
	}
	
	public void AddConnection(Neuron n)
	{
		Random rnd = new Random();
		for(Connection c : connections)
		{
			if(c.from == n)
			{
				return;
			}
		}	
		connections.add(new Connection(n, this, ((rnd.nextDouble() * 2.0f) - 1.0f) * Math.sqrt(2.0/100.0)));
	}
	
	public void AddConnection(Neuron n, double weight)
	{
		for(Connection c : connections)
		{
			if(c.from == n)
			{
				return;
			}
		}	
		connections.add(new Connection(n, this, weight));
	}
	
	public void CalculateOutput(double value)
	{
		output = 0.0;
		input = value;
		
		for(Connection c : connections)
		{
			input += (c.weight * c.from.output);
		}
		
		input *= dropoutMultiplier;
		output = activation.applyActivationFunction(input);
		
//		switch(neuron_type)
//		{
//		case INPUT:
//			output = value;
//			break;
//		case HIDDEN:
//		case OUTPUT:
//			
//			for(Connection c : connections)
//			{
//				input += (c.weight * c.from.output);
//			}
//			
//			input *= dropoutMultiplier;
//			output = activation.applyActivationFunction(input);
//			break;
//			
//		default:
//			System.out.println("ERROR NO VALID NEURON TYPE");
//			break;
//		}
	}
	
	public void postFeedForward(List<Neuron> others)
	{
		output = activation.postFeedForward(output, others, this);
	}
	
	private void maxNormWeights(double K)
	{
		double wMagnitude = 0.0;
		
		for(Connection c : connections)
		{
			wMagnitude += Math.pow(c.weight, 2.0);
		}
		
		wMagnitude = Math.sqrt(wMagnitude);
		
		if(wMagnitude > K)
		{
			wMagnitude = K/wMagnitude;
			
			for(Connection c : connections)
			{
				c.weight *= wMagnitude;
			}
		}
	}
	
	public void CalculateError(double expectedOutput)
	{
		switch(neuron_type)
		{
		case HIDDEN:

			//ONLY FOR SIGMOID
			error *= activation.derivateActivationFunction(input, output);
			
			break;
			
		case OUTPUT:
			error = 0.0;
			if(expectedOutput == output)
			{
				error = 0;
				return;
			}
			
			error = (expectedOutput - output) * activation.derivateActivationFunction(input, output);
			break;
			
		default:
			System.out.println("ERROR NO VALID NEURON TYPE");
			break;
		}
		
		if(Double.isNaN(error)) 
			{
				error = 0.0;
			}
		
//		error *= dropoutMultiplier;
	}
	
	public void GetAccumulatedError(ArrayList<Neuron> nextLayerNeurons)
	{
		error = 0.0;
		
		for(Neuron nextLayerNeuron : nextLayerNeurons)
		{
			for(Connection c : nextLayerNeuron.connections)
			{
				if(c.from == this)
				{
					error += (c.weight * nextLayerNeuron.error);
				}
			}
		}
	}
	
	public void CorrectWeights(double learning_rate)
	{
		for(Connection c : connections)
		{
			c.delta_weight = learning_rate * error * c.from.output;
			c.weight += c.delta_weight;
			c.delta_weight = 0.0f;
		}
	}
	
	public double AccumulateWeights(double learning_rate, double momentum)
	{
		double highestDelta = 0.0;
		for(Connection c : connections)
		{
//			c.previous_delta = momentum * c.previous_delta + (error * c.from.output);
//			c.previous_delta = (learning_rate * error * c.from.output) + momentum*c.previous_delta;
			c.previous_delta = (learning_rate * error * c.from.output);
//			c.previous_delta = (momentum * c.previous_delta) - (learning_rate * error * c.from.output);
			c.delta_weight -= c.previous_delta;
			
			if(Math.abs(c.delta_weight) > highestDelta)
			{
				highestDelta = Math.abs(c.delta_weight);
			}
		}
		
		return highestDelta;
	}
	
	public void ChangeWeights(double learning_rate)
	{		
		for(Connection c : connections)
		{
			c.weight -= c.delta_weight;
			c.delta_weight = 0.0f; //reset that delta weight!
//			c.previous_delta = 0.0;
		}
		
		maxNormWeights(3); //Maybe this instead of modifying the weights is a regulator? and is the same for when we 
	}
	
	public double ApplyActivationFunction(double output)
	{
		//FOR NOW ONLY SIGMOID
		double return_value = (1.0/(1.0 + Math.pow(Math.E, (-1.0*output))));
		return return_value;
	}
}

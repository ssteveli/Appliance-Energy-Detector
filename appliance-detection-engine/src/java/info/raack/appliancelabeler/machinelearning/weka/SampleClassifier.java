package info.raack.appliancelabeler.machinelearning.weka;

import java.io.Serializable;

import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

public class SampleClassifier implements Classifier, Serializable {
	private static final long serialVersionUID = 6396336782446225852L;

	private final Instances instances;
	
	public SampleClassifier(Instances instances) {
		this.instances = instances;
	}
	
	@Override
	public void buildClassifier(Instances data) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double classifyInstance(Instance instance) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Capabilities getCapabilities() {
		try {
			return Capabilities.forInstances(instances);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

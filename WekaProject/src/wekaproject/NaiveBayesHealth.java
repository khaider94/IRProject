/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author k_hai
 */
public class NaiveBayesHealth {
    
    public static void main(String[] args) throws Exception{
        //Data file on which the classifier is applied
        DataSource source = new DataSource("src/files/data1.arff");
        Instances trainingData = new Instances(source.getDataSet());
        trainingData.setClassIndex(trainingData.numAttributes()-1);
        // Creating of the classifier 
        Classifier naiveBayes = new NaiveBayes();
        naiveBayes.buildClassifier(trainingData);
        // Output the classifier
        System.out.println("\nClassifier Output: " + naiveBayes);
        // Evaluating the classifier
        Evaluation eval = new Evaluation(trainingData);
            eval.crossValidateModel(naiveBayes, trainingData, 10, new Random(1));
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));   
            System.out.println(eval.toMatrixString());
            System.out.println(eval.toClassDetailsString());
            
    }
}

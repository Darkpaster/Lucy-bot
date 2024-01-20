package org.darkpaster.NN;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.UnaryOperator;

public class NeuralNetwork {

    private double learningRate;
    private Layer[] layers;
    private UnaryOperator<Double> activation;
    private UnaryOperator<Double> derivative;

    public NeuralNetwork(double learningRate, UnaryOperator<Double> activation, UnaryOperator<Double> derivative, int... sizes) {
        this.learningRate = learningRate;
        this.activation = activation;
        this.derivative = derivative;
        layers = new Layer[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            int nextSize = 0;
            if(i < sizes.length - 1) nextSize = sizes[i + 1];
            layers[i] = new Layer(sizes[i], nextSize);
            for (int j = 0; j < sizes[i]; j++) {
                layers[i].biases[j] = Math.random() * 2.0 - 1.0;
                for (int k = 0; k < nextSize; k++) {
                    layers[i].weights[j][k] = Math.random() * 2.0 - 1.0;
                }
            }
        }
    }

    public double[][] input;
    public boolean[] bears;

    public void learnImg(int epoch) {
        epoch++;
        int samples = 276;
        BufferedImage[] images = new BufferedImage[samples];
        bears = new boolean[samples];
        File[] imagesFiles = new File("animals").listFiles();
        for (int i = 0; i < samples; i++) {
            try {
                assert imagesFiles != null;
                images[i] = ImageIO.read(imagesFiles[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bears[i] = imagesFiles[i].getName().startsWith("bear");
        }
        for (int j = 0; j < images.length; j++) {
            images[j] = resizeImage(images[j], (int) Math.sqrt(layers[0].size), (int) Math.sqrt(layers[0].size));
        }
        //System.out.println(images[2].getHeight());
        input = new double[samples][];
        for (int i = 0; i < samples; i++) {
            input[i] = new double[images[i].getHeight()*images[i].getWidth()];
            for (int y = 0; y < images[i].getHeight(); y++) {
                for (int x = 0; x < images[i].getWidth(); x++) {
                    input[i][y * images[i].getWidth() + x] = (images[i].getRGB(x, y) & 0xff) / (255.0 * 3);
                }
            }
        }
        for (int k = 1; k < epoch; k++) {
            double rights = 0, errors = 0;
            for (int i = 0; i < 100; i++) {
                int index = (int)(Math.random() * samples);
                double[] outputs = feedForward(input[index]);
                double target = bears[index] ? 1 : 0;
                errors += (target - outputs[0]) * (target - outputs[0]);
                if (target - outputs[0] < 0.5 && target - outputs[0] > -0.5) {
                    rights++;
                }
                backpropagation(new double[]{target});
                // System.out.println("Target: "+target+"|Real: "+neurons[neurons.length-1][0]);
            }
            System.out.println("Epoch: " + k + "  errors: " + errors + "  rights: " + rights);
        }
    }

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public double[] feedForward(double[] inputs) {
        System.arraycopy(inputs, 0, layers[0].neurons, 0, inputs.length);
        for (int i = 1; i < layers.length; i++)  {
            Layer l = layers[i - 1];
            Layer l1 = layers[i];
            for (int j = 0; j < l1.size; j++) {
                l1.neurons[j] = 0;
                for (int k = 0; k < l.size; k++) {
                    l1.neurons[j] += l.neurons[k] * l.weights[k][j];
                }
                l1.neurons[j] += l1.biases[j];
                l1.neurons[j] = activation.apply(l1.neurons[j]);
            }
        }
        return layers[layers.length - 1].neurons;
    }

    public void backpropagation(double[] targets) {
        double[] errors = new double[layers[layers.length - 1].size];
        for (int i = 0; i < layers[layers.length - 1].size; i++) {
            errors[i] = targets[i] - layers[layers.length - 1].neurons[i];
        }
        for (int k = layers.length - 2; k >= 0; k--) {
            Layer l = layers[k];
            Layer l1 = layers[k + 1];
            double[] errorsNext = new double[l.size];
            double[] gradients = new double[l1.size];
            for (int i = 0; i < l1.size; i++) {
                gradients[i] = errors[i] * derivative.apply(layers[k + 1].neurons[i]);
                gradients[i] *= learningRate;
            }
            double[][] deltas = new double[l1.size][l.size];
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    deltas[i][j] = gradients[i] * l.neurons[j];
                }
            }
            for (int i = 0; i < l.size; i++) {
                errorsNext[i] = 0;
                for (int j = 0; j < l1.size; j++) {
                    errorsNext[i] += l.weights[i][j] * errors[j];
                }
            }
            errors = new double[l.size];
            System.arraycopy(errorsNext, 0, errors, 0, l.size);
            double[][] weightsNew = new double[l.weights.length][l.weights[0].length];
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    weightsNew[j][i] = l.weights[j][i] + deltas[i][j];
                }
            }
            l.weights = weightsNew;
            for (int i = 0; i < l1.size; i++) {
                l1.biases[i] += gradients[i];
            }
        }
    }

}
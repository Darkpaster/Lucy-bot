package org.darkpaster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Word2vecNN {
    enum AF {
        SIGMOID, BINARY, SOFTMAX
    }

    private byte AFU = 0;

    private final double LR;

    private double[][] neurons;
    private double[][][] weights;
    private double[][] embeddings;
    private double[] biases;

    private double output = 1;
    private double[] prIn;

    Word2vecNN(double learningRate, AF activationFunction, int... neurNum) {
        LR = learningRate;
        setAF(activationFunction);
        init(neurNum);
        prIn = new double[neurons[0].length];
    }

    Word2vecNN(double learningRate, int... neurNum) {
        init(neurNum);
        LR = learningRate;
    }

    private void init(int[] neurNum) {
        neurons = new double[neurNum.length][];
        for (int i = 0; i < neurons.length; i++) {
            neurons[i] = new double[neurNum[i]];
            Arrays.fill(neurons[i], 0);
        }
        embeddings = new double[neurons[0].length][neurons[1].length];
        weights = new double[neurons.length - 1][][];
        for (int i = 1; i < neurons.length; i++) {
            weights[i - 1] = new double[neurons[i - 1].length][];
            for (int j = 0; j < weights[i - 1].length; j++) {
                weights[i - 1][j] = new double[neurons[i].length];
                Arrays.fill(weights[i - 1][j], Math.random());
            }
        }
    }

    void learn(int epoch, ArrayList<ArrayList<String>> words, ArrayList<String> vocabulary) {
        epoch++;
        for (int i = 1; i < epoch; i++) {
            double rights = 0, errors = 0;
            for (ArrayList<String> proffer : words) {
                for (int j = 0; j < proffer.size(); j++) {
                    Arrays.fill(neurons[0], 0);
                    neurons[0][vocabulary.indexOf(proffer.get(j))] = 1;
                    feedForward();
                    ArrayList<String> wordTargets = new ArrayList<>();
                    for (int k = 3; k < 3; k++) {
                        if (k == 0) continue;
                        try {
                            wordTargets.add(proffer.get(j + k));
                        } catch (Exception ignored) {
                        }
                    }
                    double[] numTargets = new double[neurons[neurons.length - 1].length];
                    Arrays.fill(numTargets, 0);
                    for (String word : wordTargets) {
                        numTargets[vocabulary.indexOf(word)] = 1;
                    }
                    backPropagation(numTargets);
                    for (int k = 0; k < numTargets.length; k++) {
                        errors += (numTargets[k] - neurons[neurons.length - 1][k]) * (numTargets[k] - neurons[neurons.length - 1][k]);
                    }
                    //System.out.println("Weights: "+Arrays.deepToString(weights[1]));
                    //System.out.println(proffer.get(j));
                    System.out.println("Target: " + Arrays.toString(numTargets) + "|Real: " + Arrays.toString(neurons[2]));
                    //System.out.println(vocabulary.get(j) + " 1 layer: "+ Arrays.deepToString(weights[0]));
                    //System.out.println(vocabulary.get(j) + " 2 layer: "+ Arrays.deepToString(weights[1]));
                }
                //System.out.println("errors: " + errors + "  rights: " + rights);
            }
        }
        for (int i = 0; i < weights[0].length; i++) {
            for (int j = 0; j < weights[0][i].length; j++) {
                embeddings[i][j] = weights[0][i][j];
            }
        }
        for (int i = 0; i < embeddings.length; i++) {
            System.out.println(vocabulary.get(i) + ": " + Arrays.toString(embeddings[i]));
        }
    }

    void learnRandom(int epoch) {
        epoch++;
        int randomTarget = (int) (Math.random() * neurons[neurons.length - 1].length);
        int randomInput = (int) (Math.random() * neurons[0].length);
        for (int k = 1; k < epoch; k++) {
            double rights = 0, errors = 0;
            for (int i = 0; i < 100; i++) {
                Arrays.fill(neurons[0], bRndom());
                feedForward();
                double[] targets = new double[neurons[neurons.length - 1].length];
                targets[randomTarget] = neurons[0][randomInput] == 1 ? 1 : 0;
                backPropagation(targets);
                for (int j = 0; j < targets.length; j++) {
                    errors += (targets[j] - neurons[neurons.length - 1][j]) * (targets[j] - neurons[neurons.length - 1][j]);
                }
                if (targets[randomTarget] - neurons[neurons.length - 1][randomTarget] < 0.5 && targets[randomTarget] - neurons[neurons.length - 1][randomTarget] > -0.5) {
                    rights++;
                }
            }
            //neurons[neurons.length-1][0] = 0;
            //System.out.println("Weights: "+weights[0]+", "+weights[1]);
            //System.out.println("Target: "+target+"|Real: "+neurons[2][0]);
            System.out.println("Epoch: " + k + "  errors: " + errors + "  rights: " + rights);
        }
    }

    void learnImg(int epoch) {
        epoch++;
        int samples = 276;
        BufferedImage[] images = new BufferedImage[samples];
        boolean[] bears = new boolean[samples];
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
            images[j] = resizeImage(images[j], 50, 50);
        }
        double[][] input = new double[samples][];
        for (int i = 0; i < samples; i++) {
            input[i] = new double[images[i].getHeight()*images[i].getWidth()];
            for (int y = 0; y < images[i].getHeight(); y++) {
                for (int x = 0; x < images[i].getWidth(); x++) {
                    input[i][y * images[i].getWidth() + x] = (images[i].getRGB(x, y) & 0xff) / (255.0 * 3);
                }
            }
        }
        //System.out.println(Arrays.deepToString(input));
        for (int k = 1; k < epoch; k++) {
            double rights = 0, errors = 0;
            for (int i = 0; i < 100; i++) {
                int index = (int)(Math.random() * samples);
                System.arraycopy(input[index], 0, neurons[0], 0, neurons[0].length);
                //neurons[0] = input[index];
                feedForward();
                double target = bears[index] ? 1 : 0;
                backPropagation(target);
                errors += (target - neurons[neurons.length - 1][0]) * (target - neurons[neurons.length - 1][0]);
                if(target == 0 && neurons[neurons.length - 1][0] < 0.5){
                    rights++;
                }else if(target == 1 && neurons[neurons.length - 1][0] >= 0.5){
                    //System.out.println("kek");
                    System.arraycopy(neurons[0], 0, prIn, 0, neurons[0].length);
                    rights++;
                }
                System.out.println("Target: "+target+"| Real: "+neurons[2][0]);
                //neurons[neurons.length-1][0] = 0;
            }
            //System.out.println("Weights: "+weights[0]+", "+weights[1]);

            System.out.println("Epoch: " + k + "  errors: " + errors + "  rights: " + rights);
        }
        output = neurons[neurons.length - 1][0];
    }


    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private void feedForward() {
        for (int i = 1; i < neurons.length; i++) {
            double[] prev = neurons[i - 1];
            for (int j = 0; j < neurons[i].length; j++) {
                for (int p = 0; p < prev.length; p++) {
                    neurons[i][j] += prev[p] * weights[i - 1][p][j];
                }
                if (AFU == 3) {
                    if (i == neurons.length - 1) {
                        neurons[i][j] = AF(neurons[i][j]);
                    }
                } else {
                    neurons[i][j] = AF(neurons[i][j]);
                }
            }
        }
    }
    public double feedForward(BufferedImage img) {
//        try {
//            img = ImageIO.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //img = resizeImage(img, 50, 50);


        double[] input = new double[img.getHeight() * img.getWidth()];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                input[y * img.getWidth() + x] = (img.getRGB(x, y) & 0xff) / (255.0 * 3);
            }
        }
        System.arraycopy(input, 0, neurons[0], 0, input.length);
        for (int i = 1; i < neurons.length; i++) {
            double[] prev = neurons[i - 1];
            for (int j = 0; j < neurons[i].length; j++) {
                for (int p = 0; p < prev.length; p++) {
                    neurons[i][j] += prev[p] * weights[i - 1][p][j];
                }
                if (AFU == 3) {
                    if (i == neurons.length - 1) {
                        neurons[i][j] = AF(neurons[i][j]);
                    }
                } else {
                    neurons[i][j] = AF(neurons[i][j]);
                }
            }
        }
        System.out.println(output);
        System.out.println(neurons[neurons.length-1][0]);
        double ebal = neurons[neurons.length-1][0];
        //neurons[neurons.length-1][0] = 0;
        return ebal;
    }

    private void backPropagation(double[] targets) {
        double[][] err = neurons;
        for (int i = 0; i < err[err.length - 1].length; i++) {
            err[err.length - 1][i] = targets[i] - neurons[neurons.length - 1][i];
        }
        for (int i = neurons.length - 1; i > 1; i--) {
            for (int j = 0; j < neurons[i].length; j++) {
                for (int p = 0; p < neurons[i - 1].length; p++) {
                    err[i - 1][p] += weights[i - 1][p][j] * err[i][j];
                }
            }
        }
        for (int i = neurons.length - 1; i > 1; i--) {
            for (int j = 0; j < neurons[i].length; j++) {
                for (int p = 0; p < neurons[i - 1].length; p++) {
                    weights[i - 1][p][j] += err[i - 1][p] * LR * neurons[i - 1][p];
                }
            }
        }
    }

    private void backPropagation(double target) {
        double[][] err = neurons;
        err[err.length - 1][0] = target - neurons[neurons.length-1][0];
        for (int i = neurons.length - 1; i > 1; i--) {
            for (int j = 0; j < neurons[i].length; j++) {
                for (int p = 0; p < neurons[i - 1].length; p++) {
                    err[i - 1][p] += weights[i - 1][p][j] * err[i][j];
                }
            }
        }
        for (int i = neurons.length - 1; i > 1; i--) {
            for (int j = 0; j < neurons[i].length; j++) {
                for (int p = 0; p < neurons[i - 1].length; p++) {
                    weights[i - 1][p][j] += err[i - 1][p] * LR * neurons[i - 1][p];
                }
            }
        }
    }

    private void setAF(AF af) {
        switch (af) {
            case BINARY:
                AFU = 1;
                break;
            case SIGMOID:
                AFU = 2;
                break;
            case SOFTMAX:
                AFU = 3;
        }
    }

    private double AF(double neuron) {
        switch (AFU) {
            case 1:
                return binary(neuron);
            case 2:
                return sigmoid(neuron);
            case 3:
                return softmax(neuron);
        }
        return neuron;
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    private double softmax(double target) {
        double sum = 0;
        for (double vector : neurons[neurons.length - 1]) {
            sum += Math.exp(vector);
        }
        return Math.exp(target) / sum;
    }

    private double binary(double x) {
        return x >= 1 ? 1 : 0;
    }

    private double bRndom() {
        return Math.random() > 0.5 ? 1 : 0;
    }
}

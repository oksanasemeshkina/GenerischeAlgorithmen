import groovy.json.internal.ArrayUtils
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import java.util.Arrays;
import java.util.Collections;


/**
 * Created by Oksana on 29.10.2016.
 */
class GenAlgorithmus {

    final int GENCOUNT = 101 //Anzahl Bits bei jedem Individuum
    final int POPULATIONSIZE = 103 //Anzahl der Individuen in the Population
    final RUN_COUNT = 150

    int[] MUSTERGEN //Individuum, mit den alle anderen vergliechen werden
    ArrayList population //Alle Hypotehesen(Individuen)
    int[] fitnes //Array mit allen Fitnes
    double crossoverReplace  //Antein, die durch Crossover erzetzt wird
    double mutationRate //Anteil, die mutiert wird
    double[] probability //Array mit allen Wahrscheinluchkeiten
    int sumFitnes //Summe von allen Fitnes in einem Durschlauf
    ArrayList populationAfterMutation
    int[] bestIndividuenIndex //Array mit Indizien von allen Individuen mit max Fitness
    ArrayList bestIndividuen //Liste mit allen Individuen mit der besten Fitnes
    ArrayList populationToMutation
    //Individuen, die mutiert werden (geänderte durch Selektion und Crossover Population minus alle Individuen mit der besten Fitness

    Random random = new Random();


    def startAlgorithmus() {
        crossoverReplace = random.nextDouble();
        crossoverReplace = 1.0 //Math.round(crossoverReplace*10)/10
        //println "Erzetzen durch Crossiver " + crossoverReplace

        int indCount = crossoverReplace * POPULATIONSIZE / 2
        indCount = indCount * 2 //Anzahl der Paaren von Individuen, die durch Crossover kopiert werden

        mutationRate = 0.5//random.nextDouble();
        //println "Mutationrate " + mutationRate

        MUSTERGEN = createHypothesis() //originale Hypothesis erzeugen
        //println ("Muster" + MUSTERGEN)

        population = createPopulation() //Population erzeugen
        //println("Population"+ population)


        for (int i = 0; i < RUN_COUNT; i++) {

            fitnes = calculateFitnes(population) //Fitnes berechnen
            //println "Fitnes " +fitnes

            sumFitnes = sumFitnes(fitnes) //Summe von allen Fitnes in der aktuellen Durchlauf
            println "Sumfitnes " + sumFitnes

            probability = calculateProbability(population, fitnes) //Wahrscheinlichkeit berechnen
            //println "Wahrscheinlichkeit " + probability

            ArrayList indToSelection = doSelection(population, indCount) //Selektion
            //println "Selektion " + indToSelection

            ArrayList indToCrossover = doCrossover(population, indCount) //Crossover
            //println "Crossover " + indToCrossover

            ArrayList followers = followersAfterCrossover(indToCrossover) //Nachfolger nach dem Crossover
            //println "Nachfolger " + followers

            population = indToSelection + followers
            //Population wird aus Individuen von der Selektion und Nachfolger nache dem Crossover zusammengesetzt
            //println "Neue Population " + newPopulation

            fitnes = calculateFitnes(population) //Fitnes berechnen
            //println "Fitnes " +fitnes

            sumFitnes = sumFitnes(fitnes)
            //println "Sumfitnes 2 " + sumFitnes

            bestIndividuenIndex = findIndWithBestFitnes(population, fitnes) //finden Indizien von Individuen mit der besten Fitnes

            bestIndividuen = bestIndividuen(bestIndividuenIndex, population) //Liste mit allen Individuen mit der besten Fitnes

            populationToMutation = populationToMutation(bestIndividuen, population)

            populationAfterMutation = doMutation(populationToMutation)
            //println "Nach der Mutation "+population

            population = addBestIndividuen(bestIndividuen, populationAfterMutation)


        }
    }


    static void main(String[] args) {
        GenAlgorithmus gen = new GenAlgorithmus()

        println()

        gen.startAlgorithmus()

    }

    /*
    Erzeugt die Hypothesis
     */

    int[] createHypothesis() {
        int[] res
        res = new int[GENCOUNT]

        for (int i = 0; i < res.length; i++) {

            int value = random.nextInt(2);
            res[i] = value
            //print res[i]
        }
        // println()
        return res
    }

    ArrayList<Integer> createPopulation() {
        ArrayList res = new ArrayList<Integer>()

        for (int i = 0; i < POPULATIONSIZE; i++) {
            res[i] = createHypothesis()
        }
        return res
    }

    /*
    Berechnet Fitnes
     */

    int[] calculateFitnes(ArrayList population) {
        int temp
        int[] fitnes
        fitnes = new int[population.size()]

        for (int i = 0; i < population.size(); i++) {
            temp = 0

            for (int j = 0; j < GENCOUNT; j++) {
                if (population[i][j] == MUSTERGEN[j]) {
                    temp++
                } else {
                    fitnes[i] = temp
                    j = population.size() - 1

                }
            }
        }
        return fitnes

    }

    /*
    Berechnet Durchschnitsfitness
     */

    int[] findIndWithBestFitnes(ArrayList population, int[] fitnes) {
        int max = 0; //größte Zahl
        int[] bestFitnesInd
        int temp = 0

        //max fitnes bestimmen
        for (int i = 0; i < fitnes.length; i++) {
            if (fitnes[i] > max) {
                max = fitnes[i];
                //indexOfMax = i
            }
        }

        //definieren, wie oft max fitnes vorkommt
        for (int i = 0; i < fitnes.length; i++) {
            if (fitnes[i] == max) {
                temp++
                //bestFitnesInd[i] = fitnes[i]
            }
        }
        //Array hat Größe temp
        bestFitnesInd = new int[temp]

        int j = 0
        for (int i = 0; i < fitnes.length; i++) {
            if (fitnes[i] == max) {
                bestFitnesInd[j] = i
                j++
            }


        }

        return bestFitnesInd
    }

    int sumFitnes(int[] fitnes) {
        int sum = 0

        for (int i = 0; i < fitnes.length; i++) {
            sum = sum + fitnes[i]
        }

        return sum
    }
/*
    ArrayList populationToMutation(int[] bestIndividuenIndex, ArrayList population) {

        for (int i = 0; i < bestIndividuenIndex.length; i++) {
            population.remove(bestIndividuenIndex[i])
            population.remove()
        }
        return population

    }
    */

    ArrayList populationToMutation(ArrayList bestIndividuen, ArrayList population) {

        for (int i = 0; i < bestIndividuen.size(); i++) {
            population.remove(bestIndividuen[i])
        }
        return population

    }


    ArrayList bestIndividuen(int[] bestIndividuenIndex, ArrayList population) {
        ArrayList bestInd = new ArrayList()

        for (int i = 0; i < bestIndividuenIndex.length; i++) {

            bestInd[i] = population[bestIndividuenIndex[i]]
        }
        return bestInd

    }

/*
Fügt die gespeicherten "besten" Individuen zu der Population hinzu
 */

    ArrayList addBestIndividuen(ArrayList bestIndividuen, ArrayList populationAfterMutation) {

        for (int i = 0; i < bestIndividuen.size(); i++) {
            if (populationAfterMutation.size() == 0) {
                populationAfterMutation.add(populationAfterMutation.size(), bestIndividuen[i])
            } else {
                populationAfterMutation.add(populationAfterMutation.size() - 1, bestIndividuen[i])
            }
        }
        return populationAfterMutation

    }
/*
Berechnet die Wahrscheinlichkeit
 */

    double[] calculateProbability(ArrayList population, int[] fitnes) {
        int sumFitnes = 0
        double[] res
        res = new double[fitnes.length]
        def probability

        //summe von allen Fitnes berechnen
        for (int i = 0; i < population.size(); i++) {
            sumFitnes += fitnes[i]
        }
        //die Wahrscheinlichkeit wird berechnet
        for (int i = 0; i < population.size(); i++) {

            if (sumFitnes == 0) {
                println("Die Fitnes von allen Individuen = 0")

            } else {
                probability = (double) (fitnes[i] / sumFitnes)
                res[i] = probability
                //  println()
                //  print("wahrscheinlichkeit ${res[i]}")
            }
        }
        return res

    }

    /*
    Wählt Individuen (Hypothesen) mit der zufällegen Wahrscheinluchkeit.
    Es wird Index von einem ausgewählten Element zurückgeliefert
     */

    int selectHypothesis(ArrayList population, double[] probability) {
        double randNum = random.nextDouble();
        //println "RandNummer "+randNum
        double sum = 0
        int index = random.nextInt(population.size())

        while (sum < randNum) {
            index = index + 1
            index = index % (population.size())
            sum = sum + probability[index]
        }
        return index
    }
/*
Such Individuen, die durch Selektion in die neue Population kopiert werden.
Es wird ArrayList mit den Individuen zurückgeliefert
 */

    ArrayList doSelection(ArrayList population, int indCount) {
        // double indCount = Math.round((1 - crossoverReplace) * POPULATIONSIZE * 100) / 100
        // indCount = (int)indCount
        int indCountSel = POPULATIONSIZE - indCount
        //Anzahl der Individuen, die durch Selektion kopiert werden
        int[] indToSelect //Array mit allen Indizies, die für Selektion in der alten Population gefunden wurden
        indToSelect = new int[indCountSel]
        ArrayList<Integer> toNewPopulation = new ArrayList<>()
        //ArrayList mit allen Individuen(kompletten Genen) in der neuen Population

        //Array mit Indizies wird gebaut
        for (int i = 0; i < indToSelect.length; i++) {
            int indIndex = selectHypothesis(population, probability)
            indToSelect[i] = indIndex
        }

        //println ("IndexArray Selektion " + indToSelect)

        //ArrayList mit allen Individuen
        for (int i = 0; i < indToSelect.length; i++) {
            toNewPopulation[i] = population[indToSelect[i]]

        }
        return toNewPopulation
    }

    /*

     */

    ArrayList doCrossover(ArrayList population, int indCount) {
        //double indCount = Math.round(crossoverReplace * POPULATIONSIZE / 2 * 100) / 100
        //Anzahl der Paaren von Individuen, die durch Crossover kopiert werden
        //indCount = (int) Math.round(indCount * 2) //Anzahl der Individuen, die durch Crossover kopiert werden


        int[] indToCrossover //Array mit allen Indizies, die für Crossover in der alten Population gefunden wurden
        indToCrossover = new int[indCount]
        ArrayList<Integer> toNewPopulation = new ArrayList<>()
        //ArrayList mit allen Individuen(kompletten Genen) in der neuen Population

        //Array mit allen Indizies
        for (int i = 0; i < indToCrossover.length; i++) {
            int indIndex = selectHypothesis(population, probability)
            indToCrossover[i] = indIndex
        }

        //println ("IndexArray Crossover " + indToCrossover)

        //ArrayList mit allen Individuen
        for (int i = 0; i < indToCrossover.length; i++) {
            toNewPopulation[i] = population[indToCrossover[i]]

        }

        return toNewPopulation

    }

    /*
    Erzeugt unter Anwendung des Crossover Operators 2 Nachkommen für jedes Paar
     */

    ArrayList followersAfterCrossover(ArrayList indToCrossover) {
        ArrayList toNewPopulation = new ArrayList<Integer>() //Liste mit alle Individuen in der neuen Population
        int[] temp1 = new int[GENCOUNT]
        int[] temp2 = new int[GENCOUNT]

        for (int i = 1; i < indToCrossover.size(); i++) {
            int randomIndex = random.nextInt(GENCOUNT) //zufälliger Index, wo die Genen getauscht werden
            //println "Random Index "+randomIndex
            int[] parent1 = indToCrossover[i - 1]
            int[] parent2 = indToCrossover[i]
            // println "parent1 "+parent1
            //println "parent2 "+parent2

            for (int j = 0; j < GENCOUNT; j++) {
                if (j < randomIndex) {
                    temp1[j] = parent1[j]
                    temp2[j] = parent2[j]

                } else {
                    temp1[j] = parent2[j]
                    temp2[j] = parent1[j]
                }
                //println "temp1 "+temp1
                //println "temp2 "+temp2
            }
            toNewPopulation[i - 1] = temp1
            toNewPopulation[i] = temp2
            // println "Population i-1 "+toNewPopulation[i - 1]
            // println "Population i "+toNewPopulation[i]
            i++
        }

        return toNewPopulation
    }

    ArrayList doMutation(ArrayList populationToMutation) {
        int indToMutation = populationToMutation.size() * mutationRate
        //println "Population "+population

        for (int i = 0; i < indToMutation; i++) {
            int randomIndex = random.nextInt(populationToMutation.size())
            int randomNumeral = random.nextInt(GENCOUNT)

            for (int j = 0; j < populationToMutation.size(); j++) {

                if (j == randomIndex) {
                    int[] temp = populationToMutation[randomIndex]
                    //println "Anfang "+temp

                    if (temp[randomNumeral] == 0) {
                        temp[randomNumeral] = 1
                    } else {
                        temp[randomNumeral] = 0
                    }
                    populationToMutation[randomIndex] = temp
                    //println "Nachdem "+ population[j]
                }

            }

        }
        return populationToMutation
    }

}

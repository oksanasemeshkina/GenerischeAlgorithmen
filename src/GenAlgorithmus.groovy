/**
 * Created by Oksana on 29.10.2016.
 */
class GenAlgorithmus {
    Random random = new Random();

    final int GENCOUNT = 100 //Anzahl Bits bei jedem Individuum
    final int POPULATIONSIZE = 100 //Anzahl der Individuen in the Population
    final int PROTECT_INDIVIDUALS = 1;
    final RUN_COUNT = 8000
    final int[] MUSTERGEN = createHypothesis()
    //originale Hypothesis erzeugen //Individuum, mit den alle anderen vergliechen werden

    ArrayList population //Alle Hypotehesen(Individuen)
    int[] fitness //Array mit allen Fitnes
    double crossoverReplace  //Antein, die durch Crossover erzetzt wird
    double mutationRate //Anteil, die mutiert wird
    double[] probability //Array mit allen Wahrscheinluchkeiten
    int sumFitness //Summe von allen Fitnes in einem Durschlauf

    def startAlgorithmus() {
        crossoverReplace = random.nextDouble();
        crossoverReplace = 0.1 //Math.round(crossoverReplace*10)/10
        int indCount = (int) (crossoverReplace * POPULATIONSIZE)
        mutationRate = 0.02//random.nextDouble();
        population = createPopulation() //Population erzeugen

        for (int i = 0; i < RUN_COUNT; i--) {
            fitness = calculateFitnes() //Fitnes berechnen
            sumFitness = 0
            fitness.each {
                sumFitness += it
            }

            println "Sumfitnes " + sumFitness

            probability = calculateProbability() //Wahrscheinlichkeit berechnen

            def bestIndividuen = protectBestIndividuals()

            ArrayList selected = doSelection(indCount) //Selektion
            ArrayList followers = doCrossover(indCount) //Crossover
            population = selected + followers

            fitness = calculateFitnes() //Fitnes berechnen

            doMutation()

            //add best individuals
            bestIndividuen.each {
                population.add(it)
            }
        }
    }

    static void main(String[] args) {
        GenAlgorithmus gen = new GenAlgorithmus()
        println()
        gen.startAlgorithmus()
    }

    /**
     * Erzeugt die Hypothesis
     */
    int[] createHypothesis() {
        int[] res
        res = new int[GENCOUNT]

        for (int i = 0; i < res.length; i++) {
            int value = random.nextInt(2);
            res[i] = value
        }
        return res
    }

    ArrayList<Integer> createPopulation() {
        ArrayList<Integer> res = new ArrayList()

        POPULATIONSIZE.times {
            res[it] = createHypothesis()
        }

        return res
    }

    /**
     * Berechnet Fitnes
     */
    int[] calculateFitnes() {
        int[] fitnes = new int[population.size()]

        for (int i = 0; i < population.size(); i++) {
            int temp = 0

            for (int j = 0; j < GENCOUNT; j++) {
                if (population[i][j] == MUSTERGEN[j]) {
                    temp++
                }
            }
            fitnes[i] = temp
        }
        return fitnes
    }

    ArrayList<Integer> protectBestIndividuals() {
        int max = 0; //größte Zahl
        ArrayList<Integer> bestIndividuals = new ArrayList<>()

        //max fitnes bestimmen
        for (int i = 0; i < fitness.length; i++) {
            if (fitness[i] > max) {
                max = fitness[i];
            }
        }

        println("Fitness of the best individual: $max")

        //alle mit max fitness sichern (max 1)
        for (int i = 0; i < fitness.length; i++) {
            if (fitness[i] == max && bestIndividuals.size() < PROTECT_INDIVIDUALS) {
                bestIndividuals.add((population[i]))
            }
        }

        //und durch entfernen schützen
       // for (int i = 0; i < bestIndividuals.size(); i++) {
       //     population.remove(bestIndividuals[i])
       // }

        return bestIndividuals
    }

    /**
     Berechnet die Wahrscheinlichkeit
     */
    double[] calculateProbability() {

        double[] res = new double[fitness.length]

        //die Wahrscheinlichkeit wird berechnet
        for (int i = 0; i < population.size(); i++) {
            if (sumFitness != 0)
                res[i] = (double) (fitness[i] / sumFitness)
        }

        return res
    }

    /**
     Wählt Individuen (Hypothesen) mit der zufällegen Wahrscheinluchkeit.
     Es wird Index von einem ausgewählten Element zurückgeliefert
     */
    int selectHypothesis() {
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

    /**
     Such Individuen, die durch Selektion in die neue Population kopiert werden.
     Es wird ArrayList mit den Individuen zurückgeliefert
     */
    ArrayList doSelection(int indCount) {
        int indCountSel = POPULATIONSIZE - indCount - PROTECT_INDIVIDUALS //Anzahl der Individuen, die durch Selektion kopiert werden
        ArrayList<Integer> toNewPopulation = new ArrayList<>()
        //ArrayList mit allen Individuen(kompletten Genen) in der neuen Population

        for (int i = 0; i < indCountSel; i++) {
            toNewPopulation.add(population[selectHypothesis()].clone());
        }

        return toNewPopulation
    }

    ArrayList doCrossover(int indCount) {
        ArrayList<Integer> amountForCrossover = new ArrayList<>()

        for (int i = 0; i < indCount; i++) {
            amountForCrossover.add(population[selectHypothesis()]);
        }

        ArrayList toNewPopulation = new ArrayList<Integer>() //Liste mit alle Individuen in der neuen Population
        int[] temp1 = new int[GENCOUNT]
        int[] temp2 = new int[GENCOUNT]

        for (int i = 1; i < indCount; i = i + 2) {
            int randomIndex = random.nextInt(GENCOUNT) //zufälliger Index, wo die Genen getauscht werden
            int[] parent1 = amountForCrossover[i - 1]
            int[] parent2 = amountForCrossover[i]

            for (int j = 0; j < GENCOUNT; j++) {
                if (j < randomIndex) {
                    temp1[j] = parent1[j]
                    temp2[j] = parent2[j]
                } else {
                    temp1[j] = parent2[j]
                    temp2[j] = parent1[j]
                }
            }
            toNewPopulation.add(temp1)
            toNewPopulation.add(temp2)
        }
        return toNewPopulation
    }

    void doMutation() {
        int indToMutation = population.size() * mutationRate

        for (int i = 0; i < indToMutation; i++) {
            int randomIndex = random.nextInt(population.size() - 1)
            int randomNumeral = random.nextInt(GENCOUNT)

            int[] temp = population[randomIndex]

            if (temp[randomNumeral] == 0) {
                temp[randomNumeral] = 1
            } else {
                temp[randomNumeral] = 0
            }

            population[randomIndex] = temp
        }
    }
}



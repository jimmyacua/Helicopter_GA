import jdk.internal.org.objectweb.asm.tree.InnerClassNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @brief Clase que permite crear un algoritmo genético para optimizar el
 *  área de aterrizaje de un helicóptero.
 *
 * Clase que permite crear un algoritmo genético para optimizar el
 *  área de aterrizaje de un helicóptero. Se parametriza la configuración
 *  siguiente:
 *
 * @author  Geovanny Cordero Valverde   (geovanny.corderovalverde@ucr.ac.cr)
 * @author  Carlos Delgado Rojas        (carlos.delgadorojas@ucr.ac.cr)
 * @author  Jimmy Acuña Díaz            (jimmy.acuna@ucr.ac.cr)
 */
public class AG {
    /**
     * Cuántos individuos hay en la población inicial
     */
    private int poblacionInicial;

    /**
     * Cuántos chapas sobreviven por generación
     */
    private double proporcionDebiles;

    /**
     * Cuántos individuos mutan de una generación a otra
     */
    private double proporcionMutar;

    /**
     * Cuántos individuos nuevos producto de cruces
     */
    private double proporcionCruzar;

    /**
     * cuántas generaciones/iteraciones tiene el AG
     */
    private int cantidadGeneraciones;

    /**
     * Tamaño del plano de aterrizaje (se asume cuadrado)
     */
    private int dimension;

    /**
     * Lista de obstáculos sobre el plano
     */
    private ArrayList<Obstaculo> obstaculos;

    /**
     * Generación de individuos de la iteración actual del AG
     */
    private ArrayList<Individuo> generacionActual;

    /**
     * Generación de individuos para la próxima iteración del AG
     */
    private ArrayList<Individuo> generacionNueva;

    /**
     * Constructor por defecto de la clase AG
     * @param poblacionInicial cantidad de individuos iniciales
     * @param proporcionDebiles cantidad de individuos no óptimos por generación
     * @param proporcionCruzar cantidad de cruces por generación
     * @param proporcionMutar cantidad de mutaciones por generación
     * @param dimensionPlano dimensión del plano, se asume cuadrado
     * @param cantidadGeneraciones cantidad de iteraciones para el algoritmo
     */
    public AG(int poblacionInicial, double proporcionDebiles,
              double proporcionCruzar, double proporcionMutar,
              int dimensionPlano, int cantidadGeneraciones){
        // parametrización de la configuración del algoritmo
        this.poblacionInicial = poblacionInicial;
        this.proporcionDebiles = proporcionDebiles;
        this.proporcionCruzar = proporcionCruzar;
        this.proporcionMutar = proporcionMutar;
        this.dimension = dimensionPlano;
        this.cantidadGeneraciones = cantidadGeneraciones;
        // inicialización de atributos
        this.obstaculos = new ArrayList<>();
        this.generacionActual = new ArrayList<>();
        this.generacionNueva = new ArrayList<>();
    }

    /**
     * Llena la lista de individuos inicial, creando estos de manera aleatoria.
     */
    public void generarPoblacionInicial(){
        for(int i = 0; i<poblacionInicial; i++){
            Individuo individuo = new Individuo(
                    (int) (Math.random()*dimension) +1,    // coordenada x
                    (int) (Math.random()*dimension)+1,    // coordenada y
                    (int) (Math.random()*(dimension/2))+1 // radio siempre menor a la dim
            );
            generacionActual.add(individuo);
        }

        //crea obstaculos para probar seleccion()
        int numObstaculos = poblacionInicial/2;
        for(int i=0;i<numObstaculos; i++){
            Obstaculo obstaculo = new Obstaculo(
                    (int) (Math.random()*dimension) +1,    // coordenada x
                    (int) (Math.random()*dimension)+1
                    );
            obstaculos.add(obstaculo);
        }
    }

    /**
     * Genera un nuevo individuo a partir de los atributos de individuos diferentes.
     * @param individuo1 individuo para cruce 1
     * @param individuo2 individuo para cruce 2
     * @return un nuevo individuo producto del cruce
     */
    public Individuo cruzar(Individuo individuo1, Individuo individuo2){
        Individuo individuo;
        // aleatorización de la herencia
        int r = ((int)(Math.random()* 10))%2;
        if(0 == r){
            individuo = new Individuo(individuo1.getX(), individuo2.getY(), individuo1.getRadio());
        } else{
            individuo = new Individuo(individuo2.getX(), individuo1.getY(), individuo2.getRadio());
        }
        return individuo;
    }

    /**
     * Modifica algún atributo del individuo para generar uno nuevo.
     * @param individuo individuo por mutar
     * @return un nuevo individuo, mutado
     */
    public Individuo mutar(Individuo individuo){
        Individuo mutante = individuo;
        int random = ((int) (Math.random()* 30))%3;
        switch (random){
            case 0:
                // muta coordenada x
                mutante.setX((int) (Math.random()*dimension));
                break;
            case 1:
                // muta coordenada y
                mutante.setY((int) (Math.random()*dimension));
                break;
            case 2:
                // muta el radio
                mutante.setRadio((int) (Math.random()*(dimension/2)));
                break;
        }
        return mutante;
    }

    /**
     * Ordena el arraylist de menor numero de colisiones a mayor numero de colisiones.
     */
    public static Comparator<Individuo> CompartorIndividuos = new Comparator<Individuo>() {
        @Override
        public int compare(Individuo i1, Individuo i2) {
            int numColisiones1 = i1.getColisiones();
            int numColisiones2 = i2.getColisiones();

            return numColisiones1-numColisiones2;
        }
    };

    /**
     * Modifica el vector de individuos, creando la nueva generación.
     */
    public ArrayList seleccionar(){
        ArrayList<Individuo> mejoresIndividuos = new ArrayList<>();
        double porcentajeBuenos = 100 - (proporcionCruzar + proporcionDebiles + proporcionMutar);
        int cantidadBuenos = (int) ((poblacionInicial * porcentajeBuenos)/100);
        int contador = 0;
        while (contador < obstaculos.size()){
            Obstaculo obstaculo = obstaculos.get(contador);
            int contadorIndividuos = 0;
            while (contadorIndividuos < generacionActual.size()) {
                Individuo individuo = generacionActual.get(contadorIndividuos);
                obstaculo.hayInterseccion(individuo);
                contadorIndividuos++;
            }
            contador++;
        }
        Collections.sort(generacionActual, CompartorIndividuos);
        System.out.println("d");
        return mejoresIndividuos;
    }



}

package datos.tripulantes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Coleccion de tripulantes de una nave, en orden de asignacion.
 * <b>INV</b>
 * - tripulantes != null (se asigna una sola vez, en el constructor)
 * - tripulantes no contiene elementos null
 * - la lista solo se modifica a traves de agregar y editar
 *   (hacia afuera se expone como vista de solo lectura o como copia)
 */
public class TripulantesCollection {

    private final List<Tripulante> tripulantes;

    /**
     * <b>POST</b>
     * - la coleccion queda vacia
     */
    public TripulantesCollection() {
        tripulantes = new ArrayList<>();
    }

    /**
     * <b>PRE</b>
     * - tripulante != null
     * <b>POST</b>
     * - tripulante queda al final de la coleccion y los anteriores no se alteran
     */
    public void agregar(Tripulante tripulante) {
        assert tripulante != null : "Tripulante invalido. No puede ser nulo";
        tripulantes.add(tripulante);
    }

    /**
     * <b>PRE</b>
     * - nombre != null y no vacio
     * - editado != null
     * <b>POST</b>
     * - si lo encuentra, el primer tripulante con ese nombre queda reemplazado por editado, en la misma
     *   posicion; el resto de la coleccion no se altera
     */
    public void editar(String nombre, Tripulante editado) {
        assert nombre != null && !nombre.trim().isEmpty() : "Nombre invalido. No puede estar vacio";
        assert editado != null : "Tripulante invalido. No puede ser nulo";
        ListIterator<Tripulante> iterador = tripulantes.listIterator();
        boolean encontrado = false;
        while (!encontrado && iterador.hasNext()) {
            if (iterador.next().getNombre().equals(nombre)) {
                iterador.set(editado);
                encontrado = true;
            }
        }
    }

    /**
     * <b>PRE</b>
     * - nombre != null
     * <b>POST</b>
     * - devuelve true si y solo si algun tripulante tiene ese nombre; no modifica nada
     */
    public boolean existeTripulante(String nombre) {
        assert nombre != null : "Nombre invalido. No puede ser nulo";
        boolean encontrado = false;
        Iterator<Tripulante> iterador = tripulantes.iterator();
        while (!encontrado && iterador.hasNext()) {
            encontrado = iterador.next().getNombre().equals(nombre);
        }
        return encontrado;
    }

    /**
     * <b>POST</b>
     * - devuelve una vista de solo lectura de los tripulantes, en orden de asignacion;
     *   intentar agregar o quitar lanza UnsupportedOperationException; no modifica nada
     */
    public List<Tripulante> getTripulantes() {
        return Collections.unmodifiableList(tripulantes);
    }

    /**
     * <b>PRE</b>
     * - cargo != null
     * <b>POST</b>
     * - devuelve una lista nueva con los tripulantes cuyo getCargo() es igual a cargo
     *   sin distinguir mayusculas de minusculas,
     *   en orden de asignacion (vacia si no hay ninguno); no modifica la coleccion
     */
    public List<Tripulante> getTripulantesPorCargo(String cargo) {
        assert cargo != null : "Cargo invalido. No puede ser nulo";
        List<Tripulante> resultado = new ArrayList<>();
        Iterator<Tripulante> iterador = tripulantes.iterator();
        while (iterador.hasNext()) {
            Tripulante tripulante = iterador.next();
            if (tripulante.getCargo().equalsIgnoreCase(cargo)) {
                resultado.add(tripulante);
            }
        }
        return resultado;
    }

    /**
     * <b>POST</b>
     * - devuelve la cantidad de tripulantes (>= 0); no modifica nada
     */
    public int cantidad() {
        return tripulantes.size();
    }
}

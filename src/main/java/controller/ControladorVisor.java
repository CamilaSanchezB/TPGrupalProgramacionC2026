package controller;

import model.Concurrencia.UniversoParalelo;
import view.VistaNaves;

/**
 * Controlador del visor. Une vista y modelo:
 *  - MVC: los ActionListener de la vista delegan la accion en el controlador,
 *    que invoca al modelo.
 *  - Observer: la vista se registra como observadora del modelo y se actualiza sola.
 */
public class ControladorVisor {

    private final VistaNaves vista;
    private final UniversoParalelo universo;

    public ControladorVisor(VistaNaves vista, UniversoParalelo universo, Runnable alVolver) {
        this.vista = vista;
        this.universo = universo;

        universo.agregarObservador(vista);

        vista.addAgregarGeneradoraListener(e -> universo.agregarGeneradora());
        vista.addAgregarConsumidoraListener(e -> universo.agregarConsumidora());
        vista.addVolverListener(e -> {
            universo.detener();
            alVolver.run();
        });
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}
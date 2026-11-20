package view;

import javax.swing.SwingUtilities;

import controller.ControladorVisor;
import model.Concurrencia.UniversoParalelo;

public class Main {
    public static void main(String[] args){
        //Test.ejecutarEscenarioA();
        //Test.ejecutarEscenarioB();
        //Test.ejecutarEscenarioC();
        //Test.ejecutarEscenarioD();
        //Test.ejecutarLiquidacion();
           SwingUtilities.invokeLater(() -> {
            VistaNaves vista = new VistaNaves();
            UniversoParalelo universo = new UniversoParalelo();
            ControladorVisor c = new ControladorVisor(vista, universo, vista::dispose);
            c.iniciar();
        });
      
    }

 }
 
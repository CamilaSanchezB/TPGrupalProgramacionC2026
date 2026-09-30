package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;

public class Consejero extends Tripulante {

    private int cantidadConsejos;
    private double importePorConsejo;

    public Consejero(String nombre, int antiguedad) {
        super(nombre, antiguedad, 600, 0.05);
        cargo = "Consejero";
        this.cantidadConsejos = 0;
        this.importePorConsejo = 2;
    }

    public int getCantidadConsejos() {
        return cantidadConsejos;
    }

    public void setCantidadConsejos(int cantidadConsejos) {
        this.cantidadConsejos = validarCantidadConsejos(cantidadConsejos);
    }

    private static int validarCantidadConsejos(int cantidadConsejos) {
        assert cantidadConsejos >= 0
                : "La cantidad de consejos no puede ser negativa: " + cantidadConsejos;
        return cantidadConsejos;
    }

    public double getImportePorConsejo() {
        return importePorConsejo;
    }

    @Override
    public double calcularSueldo() {
        return remuneracion + remuneracion * porcentajeAntiguedad * antiguedad
                + cantidadConsejos * importePorConsejo;
    }

    @Override
    public List<ConceptoHaber> getListaConceptos() {
        List<ConceptoHaber> conceptos = new ArrayList<ConceptoHaber>();
        conceptos.add(new ConceptoHaber("Remuneracion por cargo (" + cargo + ")", remuneracion));
        if (antiguedad > 0) {
            conceptos.add(new ConceptoHaber(
                    "Adicional por antiguedad (" + antiguedad + " anios)",
                    remuneracion * porcentajeAntiguedad * antiguedad));
        }
        if (cantidadConsejos > 0) {
            conceptos.add(new ConceptoHaber(
                    "Adicional por consejos registrados (" + cantidadConsejos + ")",
                    cantidadConsejos * importePorConsejo));
        }
        return conceptos;
    }
}

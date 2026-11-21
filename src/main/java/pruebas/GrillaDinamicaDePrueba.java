package pruebas;

import java.util.List;

import model.Coordenada;
import model.Universo.ElementoDelUniverso;
import model.Universo.UniversoDinamico;

/**
 * Grilla de prueba que además tiene nave y objetivo, para probar misiones completas con un
 * escenario determinista (el Tablero de la cátedra ubica los elementos al azar).
 * Destino no mueve nada: turnoDestino() no hace nada.
 */
public class GrillaDinamicaDePrueba extends GrillaDePrueba implements UniversoDinamico {
    private Coordenada nave;
    private Coordenada objetivo;

    public GrillaDinamicaDePrueba(int ancho, int alto, Coordenada nave, Coordenada objetivo) {
        super(ancho, alto);
        this.nave = nave;
        this.objetivo = objetivo;
    }

    @Override
    public void ubicarNave(Coordenada c) {
        if (!contiene(c))
            throw new IllegalArgumentException("La nave debe ubicarse dentro del universo");
        nave = c;
    }

    @Override
    public Coordenada getPosicionNave() { return nave; }

    @Override
    public Coordenada getObjetivo() { return objetivo; }

    @Override
    public ElementoDelUniverso inspeccionarNave() {
        List<ElementoDelUniverso> aca = elementosEn(nave);
        return aca.isEmpty() ? null : aca.get(0);
    }

    @Override
    public void turnoDestino() { /* escenario determinista: nada se mueve */ }
}
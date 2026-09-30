package datos.tripulantes;

import java.util.List;

public interface Liquidable {
    public abstract double calcularSueldo();
    public abstract List<ConceptoHaber> getListaConceptos();
    public String getConceptos();
}

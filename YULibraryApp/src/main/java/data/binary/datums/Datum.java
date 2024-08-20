package data.binary.datums;

import model.Storable;

import java.io.Serializable;


// Interface for Objects representing binary data.
public interface Datum extends Serializable, Storable {

    String getId();

    boolean getFlag();

    void setFlag(boolean b);

}

package com.com.jpbc;

import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pbc.curve.PBCTypeACurveGenerator;

public abstract class TWO {
	int rBits = 160;
	int qBits = 512;

	// JPBC Type A pairing generator...
	TypeACurveGenerator pg = new TypeACurveGenerator(rBits, qBits);
	// PBC Type A pairing generator...
	PBCTypeACurveGenerator pbcPg = new PBCTypeACurveGenerator(rBits, qBits);


	/*// JPBC Type A pairing generator...
	ParametersGenerator pg1 = new TypeACurveGenerator(rBits, qBits);
	// PBC Type A pairing generator...
	ParametersGenerator pbcPg2 = new PBCTypeACurveGenerator(rBits, qBits);*/
}

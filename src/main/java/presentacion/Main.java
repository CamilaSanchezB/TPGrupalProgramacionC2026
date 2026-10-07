LNaveFactory fact = new NaveFactory();

        Nave naveCombate = NaveFactory.crearNave("combate");

        Tripulante t = new Capitan("spock", 50);
        Vulcano spock = new Vulcano(t, 10);
        naveCombate.asignarTripulante(spock);

        AsistenteDeComando jarvis = null;
        MisionRecoleccion m = new MisionRecoleccion(jarvis);
        

    }
}

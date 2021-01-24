package mods.banana.economy2.bounties;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

public class BountyHandler {
    private static ArrayList<Bounty> bounties = new ArrayList<>();

    public static void add(Bounty bounty) { bounties.add(bounty); }
    public static ArrayList<Bounty> getBounties() { return bounties; }
}

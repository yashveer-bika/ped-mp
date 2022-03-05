package ped;


import java.util.Set;

interface Controller {
    Set<Phase> selectBestPhaseSet();

    void updateTime(double newTime);
}

from tokenize import Double
from numpy import size
import pandas as pd
import matplotlib.pyplot as plt
import sys

from pyparsing import col
# make graph of stable demand

"""
/opt/anaconda3/envs/pedMP/bin/python /Users/yashveerbika/project/ped-mp/src/util/plot.py network-level-occupancy data=SiouxFalls_control=pedMP_tol_time=30_veh_ped_sim_ds=016405191040039063_dur=19800_ts=15_id=1656435673279.csv data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=017176580697500002_dur=19800_ts=15_id=1656460452411.csv
"""

def main(plot_type, filepaths):

    # make a graph
    fig= plt.figure() 

    plt.xlabel("sim time")

    if plot_type == "stable-def-region":
        plt.ylabel("stable-def-region")
    elif plot_type == "network-level-occupancy":
        plt.ylabel("network-level-occupancy")
    elif plot_type == "avg-link-tt":
        plt.ylabel("avg-link-tt")
    elif plot_type == "ped_network_level_occupancy":
        plt.ylabel("ped_network_level_occupancy")
    else:
        assert 1==2

    colors = ['bo', 'ro', 'go', 'co', 'ko', 'mo', 'yo']

    assert len(filepaths) <= len(colors)

    # colors = ['b', 'r', 'g', 'c', 'k', 'm', 'y']

    for idx, f_path in enumerate(filepaths):
        print(f_path)
        # decode filepath into controller, tolerance time, demand scale
        print( f_path.split("_") )

        color = colors[idx]
        df = pd.read_csv(f_path)
        # TODO: make legend for each line on the plot

        if plot_type == "stable-def-region":
            plt.plot(df["sim time"], df["stable-def-region"], color, markersize=0.175)
        elif plot_type == "network-level-occupancy":
            plt.plot(df["sim time"], df["network-level occupancy"], color, markersize=0.175)
        elif plot_type == "avg-link-tt":
            plt.plot(df["sim time"], df["avg link tt"], color, markersize=0.175)
        elif plot_type == "ped_network_level_occupancy":
            plt.plot(df["sim time"], df["ped_network_level_occupancy"], color, markersize=0.175)

    # title = filepath.split("id")[0]
    # plt.title(title)

    plt.legend(loc="lower right")
    plt.show()

if __name__ == "__main__":
    print(sys.argv)
    plot_type = sys.argv[1]
    for arg in sys.argv[2:]:
        assert ".csv" in arg
    
    main(plot_type, sys.argv[2:])
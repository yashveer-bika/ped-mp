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

def main():

    # make a graph
    fig= plt.figure() 

    plt.xlabel("Simulation time (Seconds)")

    # plt.ylabel("stable-def-region")
    # plt.ylabel("network-level-occupancy")
    # plt.ylabel("Average queue length")
    # plt.ylabel("Average vehicle delay (Seconds)")
    plt.ylabel("Average pedestrian delay (Seconds)")
    # plt.ylabel("ped_network_level_occupancy")

    colors = ['bo', 'ro', 'go', 'co', 'ko', 'mo', 'yo']

    # colors = ['b', 'r', 'g', 'c', 'k', 'm', 'y']

    ax = plt.gca()


    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=6k_dur=19800_ts=15_id=????.csv"
    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=7k_dur=19800_ts=15_id=????.csv"
    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=8k_dur=19800_ts=15_id=????.csv"
    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=9k_dur=19800_ts=15_id=????.csv"
    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=10k_dur=19800_ts=15_id=????.csv"

    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=0522857666015625_dur=19800_ts=15_id=1657054807194.csv"
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=30_veh_ped_sim_ds=016405191040039063_dur=19800_ts=15_id=1656435673279.csv"
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=017176580697500002_dur=19800_ts=15_id=1656460452411.csv"
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=90_veh_ped_sim_ds=017176580697500002_dur=19800_ts=15_id=1656459871073.csv"
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=120_veh_ped_sim_ds=01896875_dur=19800_ts=15_id=1656445737749.csv"
    # df = pd.read_csv(filepath)
    # df.plot(kind='line',x=x_axis, y=y_axis, ax=ax)plot

    # plt.plot(df["sim time"], df["avg delay"], 'red', markersize=0.175, label="pedMP w/ 120s tolerance")
    

    markersize_ = 10.0
    freq = 120
    markers_on = range(0, 1320, 132)

    # title = "vehMP_stability_plot.png"
    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=6k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"], df["network-level occupancy"], 'tab:blue', markersize=markersize_, label="vehMP (6k demand)", linestyle='solid', marker=".", markevery=markers_on)
    # # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:blue', markersize=markersize_, label="vehMP (6k demand)", linestyle='solid', marker=".")

    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=7k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"], df["network-level occupancy"], 'tab:orange', markersize=markersize_, label="vehMP (7k demand)", linestyle='dotted', marker="v", markevery=markers_on)
    # # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:orange', markersize=8.0, label="vehMP (7k demand)", linestyle='dotted', marker="v")

    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=8k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"], df["network-level occupancy"], 'tab:green', markersize=markersize_, label="vehMP (8k demand)", linestyle='dashed', marker="X", markevery=markers_on)
    # # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:green', markersize=markersize_, label="vehMP (8k demand)", linestyle='dashed', marker="X")
    
    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=9k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"], df["network-level occupancy"], 'tab:red', markersize=markersize_, label="vehMP (9k demand)", linestyle='dashdot', marker="s", markevery=markers_on)
    # # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:red', markersize=7.0, label="vehMP (9k demand)", linestyle='dashdot', marker="s")
    
    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=10k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"], df["network-level occupancy"], 'tab:purple', markersize=markersize_, label="vehMP (10k demand)", linestyle=(0, (1, 1)), marker="*", markevery=markers_on)
    # # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:purple', markersize=markersize_, label="vehMP (10k demand)", linestyle=(0, (1, 1)), marker="*")


    #####

    # title = "curve_at_2k.png"

    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=30_veh_ped_sim_ds=2k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:blue', markersize=markersize_, label="pedMP (2k demand, 30s tolerance)", linestyle='solid', marker=".")

    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=2k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:orange', markersize=8.0, label="pedMP (2k demand, 60s tolerance)", linestyle='dotted', marker="v")

    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=90_veh_ped_sim_ds=2k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:green', markersize=markersize_, label="pedMP (2k demand, 90s tolerance)", linestyle='dashed', marker="X")
    
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=120_veh_ped_sim_ds=2k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:red', markersize=7.0, label="pedMP (2k demand, 120s tolerance)", linestyle='dashdot', marker="s")
    
    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=2k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:purple', markersize=markersize_, label="vehMP (2k demand)", linestyle=(0, (1, 1)), marker="*")



    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=30_veh_ped_sim_ds=2.2k_dur=19800_ts=15_id=0.csv"
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=2.2k_dur=19800_ts=15_id=0.csv"
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=90_veh_ped_sim_ds=2.2k_dur=19800_ts=15_id=0.csv"
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=120_veh_ped_sim_ds=2.2k_dur=19800_ts=15_id=0.csv"
    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=2.2k_dur=19800_ts=15_id=0.csv"

    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=30_veh_ped_sim_ds=2.2k_dur=19800_ts=15_id=0.csv"
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=2.2k_dur=19800_ts=15_id=0.csv"
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=90_veh_ped_sim_ds=2.2k_dur=19800_ts=15_id=0.csv"
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=120_veh_ped_sim_ds=2.2k_dur=19800_ts=15_id=0.csv"
    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=2.2k_dur=19800_ts=15_id=0.csv"


    # #####

    # title = "delay_dynamics_at_2k_demand.png"

    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=30_veh_ped_sim_ds=2k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # print(df["avg delay"])
    # plt.plot(df["sim time"].iloc[::freq], df["avg delay"].rolling(window=7).mean().iloc[::freq], 'tab:blue', markersize=markersize_, label="pedMP (2k demand, 30s tolerance)", linestyle='solid', marker=".")

    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=2k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # print(df["avg delay"])
    # plt.plot(df["sim time"].iloc[::freq], df["avg delay"].rolling(window=7).mean().iloc[::freq], 'tab:orange', markersize=8.0, label="pedMP (2k demand, 60s tolerance)", linestyle='dotted', marker="v")

    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=120_veh_ped_sim_ds=2k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # print(df["avg delay"])
    # plt.plot(df["sim time"].iloc[::freq], df["avg delay"].rolling(window=7).mean().iloc[::freq], 'tab:red', markersize=7.0, label="pedMP (2k demand, 90s tolerance)", linestyle='dashdot', marker="s")

    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=90_veh_ped_sim_ds=2k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # print(df["avg delay"])
    # plt.plot(df["sim time"].iloc[::freq], df["avg delay"].rolling(window=7).mean().iloc[::freq], 'tab:green', markersize=markersize_, label="pedMP (2k demand, 120s tolerance)", linestyle='dashed', marker="X")
    
    # filepath = "csv/data=SiouxFalls_control=vehMP_veh_only_sim_ds=2k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # print(df["avg delay"])
    # plt.plot(df["sim time"].iloc[::freq], df["avg delay"].rolling(window=7).mean().iloc[::freq], 'tab:purple', markersize=markersize_, label="vehMP (2k demand)", linestyle=(0, (1, 1)), marker="*")


    #####

    # title = "ped_delay_dynamics_at_2k_demand.png"

    filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=30_veh_ped_sim_ds=2k_dur=19800_ts=15_id=1.csv"
    df = pd.read_csv(filepath)
    # print(df["avg delay"])
    # plt.plot(df["sim time"].iloc[::freq], df["avg ped delay"].rolling(window=7).mean().iloc[::freq] / df["ped_network_level_occupancy"].iloc[::freq] , 'tab:blue', markersize=markersize_, label="pedMP (2k demand, 30s tolerance)", linestyle='solid', marker=".")
    # plt.plot(df["sim time"].iloc[::freq], df["avg ped delay"].rolling(window=16).mean().iloc[::freq] , 'tab:blue', markersize=6.0, label="pedMP (2k demand, 30s tolerance)", linestyle='solid', marker=".")
    # plt.plot(df["sim time"], df["avg ped delay"].rolling(window=16).mean(), 'tab:blue', markersize=10.0, label="pedMP (2k demand, 30s tolerance)", linestyle='solid', marker=".", markevery=markers_on)
    plt.plot(df["sim time"], df["avg ped delay"].rolling(window=8).mean(), 'tab:blue', markersize=10.0, label="pedMP (2k demand, 30s tolerance)", linestyle='solid', marker=".", markevery=markers_on)

    filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=2k_dur=19800_ts=15_id=1.csv"
    df = pd.read_csv(filepath)
    # print(df["avg delay"])
    # plt.plot(df["sim time"].iloc[::freq], df["avg ped delay"].rolling(window=7).mean().iloc[::freq] / df["ped_network_level_occupancy"].iloc[::freq], 'tab:orange', markersize=8.0, label="pedMP (2k demand, 60s tolerance)", linestyle='dotted', marker="v")
    plt.plot(df["sim time"].iloc[::freq], df["avg ped delay"].rolling(window=16).mean().iloc[::freq], 'tab:orange', markersize=6.0, label="pedMP (2k demand, 60s tolerance)", linestyle='dotted', marker="v")

    filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=90_veh_ped_sim_ds=2k_dur=19800_ts=15_id=1.csv"
    df = pd.read_csv(filepath)
    # print(df["avg delay"])
    # plt.plot(df["sim time"].iloc[::freq], df["avg ped delay"].rolling(window=7).mean().iloc[::freq] / df["ped_network_level_occupancy"].iloc[::freq], 'tab:red', markersize=7.0, label="pedMP (2k demand, 90s tolerance)", linestyle='dashdot', marker="s")
    plt.plot(df["sim time"].iloc[::freq], df["avg ped delay"].rolling(window=16).mean().iloc[::freq], 'tab:red', markersize=6.0, label="pedMP (2k demand, 90s tolerance)", linestyle='dashdot', marker="s")

    filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=120_veh_ped_sim_ds=2k_dur=19800_ts=15_id=1.csv"
    df = pd.read_csv(filepath)
    # print(df["avg delay"])
    plt.plot(df["sim time"].iloc[::freq], df["avg ped delay"].rolling(window=16).mean().iloc[::freq], 'tab:green', markersize=6.0, label="pedMP (2k demand, 120s tolerance)", linestyle='dashed', marker="X")
    
    filepath = "csv/data=SiouxFalls_control=vehMP_veh_ped_sim_ds=2k_dur=19800_ts=15_id=1.csv"
    df = pd.read_csv(filepath)
    # print(df["avg delay"])
    plt.plot(df["sim time"].iloc[::freq], df["avg ped delay"].rolling(window=16).mean().iloc[::freq], 'tab:purple', markersize=markersize_, label="vehMP (2k demand)", linestyle=(0, (1, 1)), marker="*")




    # title = "pedMP_tol_60_at_varying_demand.png"

    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=1.5k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:blue', markersize=markersize_, label="pedMP (1.5k demand, 60s tolerance)", linestyle='solid', marker=".")

    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=2k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:orange', markersize=8.0, label="pedMP (2.0k demand, 60s tolerance)", linestyle='dotted', marker="v")

    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=2.5k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:green', markersize=markersize_, label="pedMP (2.5k demand, 60s tolerance)", linestyle='dashed', marker="X")
    
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=3.0k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:red', markersize=7.0, label="pedMP (3.0k demand, 60s tolerance)", linestyle='dashdot', marker="s")
    
    # filepath = "csv/data=SiouxFalls_control=pedMP_tol_time=60_veh_ped_sim_ds=3.5k_dur=19800_ts=15_id=0.csv"
    # df = pd.read_csv(filepath)
    # plt.plot(df["sim time"].iloc[::freq], df["network-level occupancy"].iloc[::freq], 'tab:purple', markersize=markersize_, label="pedMP (3.5k demand, 60s tolerance)", linestyle=(0, (1, 1)), marker="*")



    # filepath = "data=SiouxFalls_control=vehMP_veh_only_sim_ds=075005_dur=19800_ts=15_id=1656371691070.csv"
    # df = pd.read_csv(filepath)
    # # df.plot(kind='line',x=x_axis, y=y_axis, ax=ax)
    # plt.plot(df["sim time"], df["network-level occupancy"], 'blue', markersize=0.175, label="vehMP w/ 11200 veh/hr")

    # title = "Average vehicle delay vs. Simulation time for vehMP at 7850 veh/hr in Sioux Falls"
    # plt.title(title)


    # for idx, f_path in enumerate(filepaths):
    #     print(f_path)
    #     # decode filepath into controller, tolerance time, demand scale
    #     print( f_path.split("_") )

    #     color = colors[idx]
    #     df = pd.read_csv(f_path)
    #     # TODO: make legend for each line on the plot

    #     if plot_type == "stable-def-region":
    #         plt.plot(df["sim time"], df["stable-def-region"], color, markersize=0.175)
    #     elif plot_type == "network-level-occupancy":
    #         # plt.plot(df["sim time"], df["network-level occupancy"], color, markersize=0.175)
    #         df.plot(kind='line',x='sim time',y='network-level occupancy',ax=ax)
    #     elif plot_type == "avg-link-tt":
    #         plt.plot(df["sim time"], df["avg link tt"], color, markersize=0.175)
    #     elif plot_type == "ped_network_level_occupancy":
    #         plt.plot(df["sim time"], df["ped_network_level_occupancy"], color, markersize=0.175)

    # title = filepath.split("id")[0]
    

    plt.legend(loc="lower right")
    # plt.savefig(title, dpi=2800)
    plt.show()

if __name__ == "__main__":
    print(sys.argv)
    # plot_type = sys.argv[1]
    # for arg in sys.argv[2:]:
    #     assert ".csv" in arg
    
    # main(plot_type, sys.argv[2:])
    main()
from tokenize import Double
import pandas as pd
import matplotlib.pyplot as plt
import sys
# make graph of stable demand

def main(filepath):
    table = []
    f = open(filepath, "r")

    # convert filepath to csv
    csv_filepath = filepath.split(".")

    sim_time = 0
    network_level_occupancy = 0
    ped_network_level_occupancy = 0
    avg_link_tt = 0
    avg_delay = 0
    end_of_time = False
    cum_network_level_occupancy = 0
    stable_def = 0

    def float_cast(splitter):
        return float(l.split(splitter)[1][:-1])

    for l in f:
        if "Sim Time: " in l:
            if end_of_time:
                stable_def = cum_network_level_occupancy / max(1, sim_time)
                table.append([sim_time, network_level_occupancy, ped_network_level_occupancy, avg_link_tt, avg_delay, stable_def])
                end_of_time = False

            sim_time = float_cast("Sim Time: ")
            # print(sim_time)
        elif "network-level occupancy: " in l:
            # stable_def = (network_level_occupancy + stable_def * max(0, sim_time - 15)) / max(1, sim_time)
            network_level_occupancy = float_cast("network-level occupancy: ")
            cum_network_level_occupancy += network_level_occupancy
            # print(network_level_occupancy)
        elif "network-level ped occupancy: " in l:
            ped_network_level_occupancy = float_cast("network-level ped occupancy: ")
            # TODO: add ped plotting as well
        elif "avg link tt: " in l:
            avg_link_tt = float_cast("avg link tt: ")
            # print(avg_link_tt)
        elif "avg delay: " in l:
            avg_delay = float_cast("avg delay: ")
            # print(avg_delay)
            end_of_time = True
        else:
            table.append([sim_time, network_level_occupancy, ped_network_level_occupancy, avg_link_tt, avg_delay, stable_def])
            end_of_time = False

    df = pd.DataFrame(table, columns=["sim time", "network-level occupancy", "ped_network_level_occupancy", "avg link tt", "avg delay", "stable-def-region"])
    
    df.to_csv("oonga.csv")

    # # make a graph
    # fig= plt.figure() 

    # # plt.plot(df["sim time"], df["stable-def-region"], 'bo', markersize=0.05)
    # plt.plot(df["sim time"], df["network-level occupancy"], 'bo', markersize=0.175)
    # # plt.plot(df["sim time"], df["ped_network_level_occupancy"], 'bo', markersize=0.175)
    # # plt.plot(df["sim time"], df["avg link tt"], 'bo', markersize=0.175)

    # # plt.xlabel("sim time")
    # # plt.ylabel("stable-def-region")
    # plt.ylabel("network-level-occupancy")
    # # plt.ylabel("avg link tt")

    # title = filepath.split("id")[0]
    # plt.title(title)
    # plt.show()

if __name__ == "__main__":
    print(sys.argv)
    filepath = sys.argv[1]
    main(filepath)
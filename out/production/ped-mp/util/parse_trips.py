# PARSE A STATIC OD file, with form like ../data/SiouxFalls/trips.txt

from ast import literal_eval
import os

path_to_file = "../../data/SiouxFalls"
file_name = "trips.txt"
out_fname = "trips_static_od_demand.txt"


f = open( os.path.join(path_to_file, file_name), "r" )
file_contents = f.read()
# print(len(file_contents))

file_lines = file_contents.split("\n")
# print(len(file_lines))

graph = {}
origin = None
adjacency_dict = {}
for i, l in enumerate(file_lines):
    if "Origin" in l:
        graph[origin] = adjacency_dict
        adjacency_dict = {}
        # print(l)
        # print(l.split("\t"))
        origin = int(l.split("\t")[1][:-1])
        # print(l)
    elif ";" in l:
        l = "{" + l.replace(";", ",") + "}"
        adjacency_dict.update(literal_eval(l))

graph[origin] = adjacency_dict

# write graph to txt file with 
with open(os.path.join(path_to_file, out_fname), 'w') as f:
    f.write(f"origin_id\tdest_id\tdemand\n")
    for origin_id in graph.keys():
        d1 = graph[origin_id]
        for dest_id in graph[origin_id]:
            d2 = d1[dest_id]
            # print(d2)
            f.write(f"{origin_id}\t{dest_id}\t{d2}\n")
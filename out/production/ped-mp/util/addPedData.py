# PARSE ALL THE NETWORK files (node/link), with form like ../data/SiouxFalls/

from ast import literal_eval
import os
import pandas as pd

path_to_file = "ped-mp/data/PQ3"
node_file_name = "nodes.txt"
links_file_name = "links.txt"

f = open( os.path.join(path_to_file, node_file_name), "r" )
file_contents = f.read()
# print(len(file_contents))

# file_lines = file_contents.split("\n")
# print(len(file_lines))
# for l in file_lines:
#     print(l)

node_df = pd.read_csv(os.path.join(path_to_file, node_file_name), sep='\t', index_col="id")

print(node_df)
print()
print(node_df.loc[1])


links_df = pd.read_csv(os.path.join(path_to_file, links_file_name), sep='\t', index_col="id")
print(links_df)
print()
print(links_df.loc[12])

# pedestrian node generation

# graph = {}
# origin = None
# adjacency_dict = {}
# for i, l in enumerate(file_lines):
#     if "Origin" in l:
#         graph[origin] = adjacency_dict
#         adjacency_dict = {}
#         # print(l)
#         # print(l.split("\t"))
#         origin = int(l.split("\t")[1][:-1])
#         # print(l)
#     elif ";" in l:
#         l = "{" + l.replace(";", ",") + "}"
#         adjacency_dict.update(literal_eval(l))

# graph[origin] = adjacency_dict

# write graph to txt file with 

# with open(os.path.join(path_to_file, node_file_name), 'a') as f:
#     for origin_id in graph.keys():
#         d1 = graph[origin_id]
#         for dest_id in graph[origin_id]:
#             d2 = d1[dest_id]
#             # print(d2)
#             f.write(f"{origin_id}\t{dest_id}\t{d2}\n")
import math
from collections import defaultdict
from typing import Dict, Tuple


class Graph:
    def __init__(self):
        self.adjacency_list = defaultdict(dict)
        self.nodes: Dict[str, Tuple[float, float]] = {}
    
    def add_node(self, node_id: str, lat: float, lng: float):
        self.nodes[node_id] = (lat, lng)
    
    def add_edge(self, node1: str, node2: str, distance: float):
        self.adjacency_list[node1][node2] = distance
        self.adjacency_list[node2][node1] = distance
    
    def calculate_distance(self, node1: str, node2: str) -> float:
        """Calculate Haversine distance between two nodes"""
        if node1 not in self.nodes or node2 not in self.nodes:
            return float('inf')
            
        lat1, lng1 = self.nodes[node1]
        lat2, lng2 = self.nodes[node2]
        
        R = 6371
        lat1_rad = math.radians(lat1)
        lat2_rad = math.radians(lat2)
        delta_lat = math.radians(lat2 - lat1)
        delta_lng = math.radians(lng2 - lng1)
        
        a = (math.sin(delta_lat/2) * math.sin(delta_lat/2) +
             math.cos(lat1_rad) * math.cos(lat2_rad) *
             math.sin(delta_lng/2) * math.sin(delta_lng/2))
        c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
        return R * c
    
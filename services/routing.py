import heapq
from typing import Dict, Optional, Tuple
from models.graph import Graph


class RoutingService:
    def __init__(self, graph: Graph):
        self.graph = graph
    
    def dijkstra_shortest_path(self, start_node: str) -> Dict[str, float]:
        """Dijkstra's algorithm to find shortest paths from start node to all centers"""
        distances = {node: float('inf') for node in self.graph.nodes}
        distances[start_node] = 0
        priority_queue = [(0, start_node)]
        
        while priority_queue:
            current_distance, current_node = heapq.heappop(priority_queue)
            
            if current_distance > distances[current_node]:
                continue
            
            for neighbor, weight in self.graph.adjacency_list[current_node].items():
                distance = current_distance + weight
                
                if distance < distances[neighbor]:
                    distances[neighbor] = distance
                    heapq.heappush(priority_queue, (distance, neighbor))
        
        return distances
    
    def find_optimal_center(self, request_id: str, relief_centers: Dict, required_resources: Dict) -> Optional[Tuple[str, float]]:
        """Find nearest relief center with sufficient resources"""
        distances = self.dijkstra_shortest_path(request_id)
        
        optimal_center = None
        min_distance = float('inf')
        
        for center_id, center in relief_centers.items():
            if center_id in distances and center.has_sufficient_resources(required_resources):
                if distances[center_id] < min_distance:
                    min_distance = distances[center_id]
                    optimal_center = center_id
        
        return (optimal_center, min_distance) if optimal_center else None
    
    def calculate_transport_cost(self, distance: float, resources: Dict[str, int]) -> float:
        """Calculate transport cost based on distance and resource volume"""
        base_cost = 50
        resource_volume = sum(resources.values()) / 100
        return distance * base_cost * (1 + resource_volume * 0.1)
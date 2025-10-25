from datetime import datetime
from typing import Dict, List


class AllocationLog:
    def __init__(self):
        self.logs = []
    
    def add_entry(self, request_id: str, center_id: str, resources: Dict[str, int], 
                  distance: float, travel_time: float, cost: float, user_selected: bool = False):
        log_entry = {
            'timestamp': datetime.now(),
            'request_id': request_id,
            'center_id': center_id,
            'resources_allocated': resources.copy(),
            'distance_km': distance,
            'travel_time_hours': travel_time,
            'transport_cost': cost,
            'user_selected': user_selected
        }
        self.logs.append(log_entry)
        
        selection_type = "USER-SELECTED" if user_selected else "AUTO-ALLOCATED"
        print(f"📋 ALLOCATION LOG: {selection_type} | Request {request_id} → {center_id}")
        print(f"   📏 Distance: {distance:.2f}km | ⏱️ Time: {travel_time:.2f}h | 💰 Cost: ₹{cost:.2f}")
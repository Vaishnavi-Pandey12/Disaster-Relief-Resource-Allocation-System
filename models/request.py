from datetime import datetime
from typing import Dict, Tuple, Optional


class DisasterRequest:
    def __init__(self, request_id: str, location: str, resources: Dict[str, int], 
                 urgency: int, timestamp: datetime = None):
        self.request_id = request_id
        self.location = location
        self.resources = resources
        self.urgency = urgency
        self.timestamp = timestamp or datetime.now()
        self.coordinates: Optional[Tuple[float, float]] = None
        self.allocated_center: Optional[str] = None
        
    def __lt__(self, other):
        if self.urgency == other.urgency:
            return self.timestamp < other.timestamp
        return self.urgency < other.urgency
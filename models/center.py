from typing import Dict


class ReliefCenter:
    def __init__(self, ngo_data: Dict):
        self.id = ngo_data['id']
        self.name = ngo_data['name']
        self.location = ngo_data['location']
        self.coordinates = ngo_data['coordinates']
        self.resources = ngo_data['resources'].copy()
        self.contact = ngo_data['contact']
        
    def has_sufficient_resources(self, required_resources: Dict[str, int]) -> bool:
        for resource, quantity in required_resources.items():
            if self.resources.get(resource, 0) < quantity:
                return False
        return True
    
    def allocate_resources(self, required_resources: Dict[str, int]) -> bool:
        if not self.has_sufficient_resources(required_resources):
            return False
        
        for resource, quantity in required_resources.items():
            self.resources[resource] -= quantity
        return True
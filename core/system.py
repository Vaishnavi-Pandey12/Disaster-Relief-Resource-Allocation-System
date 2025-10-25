import heapq
import time
import threading
from typing import Dict, List, Optional, Tuple

from models.request import DisasterRequest
from models.center import ReliefCenter
from models.graph import Graph
from services.geocoder import Geocoder
from services.routing import RoutingService
from core.allocation_log import AllocationLog


class DisasterReliefAllocationSystem:
    def __init__(self, ngo_json_file: str = "ngos.json"):
        self.request_queue = []
        self.relief_centers = {}
        self.graph = Graph()
        self.allocation_log = AllocationLog()
        self.geocoder = Geocoder(ngo_json_file)
        self.routing_service = RoutingService(self.graph)
        
        self.is_running = False
        self.request_counter = 0
        
        self.initialize_system()

    def initialize_system(self):
        """Initialize relief centers and build graph"""
        print("🔄 Initializing Disaster Relief Allocation System...")
        
        for ngo_data in self.geocoder.ngos_data:
            center = ReliefCenter(ngo_data)
            self.relief_centers[center.id] = center
            
            self.graph.add_node(
                center.id, 
                center.coordinates['lat'], 
                center.coordinates['lng']
            )
        
        print(f"✅ System initialized with {len(self.relief_centers)} relief centers")

    def get_user_request(self):
        """Get disaster request details from user"""
        from utils.helpers import get_user_input
        
        return get_user_input()

    def find_nearby_ngos_for_user(self, location: str, max_results: int = 5):
        """Find and display nearby NGOs for a user-provided address"""
        return self.geocoder.find_nearby_ngos_for_display(location, max_results)

    def allocate_to_specific_ngo(self, request_id: str, ngo_id: str, resources: Dict[str, int]):
        """Allocate resources to a specific NGO selected by user"""
        if ngo_id not in self.relief_centers:
            print(f"❌ NGO {ngo_id} not found!")
            return False
        
        center = self.relief_centers[ngo_id]
        distance = self.graph.calculate_distance(request_id, ngo_id)
        
        travel_time = distance / 40  # km/h
        transport_cost = self.routing_service.calculate_transport_cost(distance, resources)
        
        if center.allocate_resources(resources):
            self.allocation_log.add_entry(
                request_id, ngo_id, resources,
                distance, travel_time, transport_cost, user_selected=True
            )
            
            print(f"\n🎉 HELP IS ON THE WAY! 🎉")
            print(f"✅ Resources allocated from {center.name}")
            print(f"📦 Resources being sent: {resources}")
            print(f"📍 From: {center.location}")
            print(f"📏 Distance: {distance:.2f} km")
            print(f"⏱️ Estimated arrival: {travel_time:.2f} hours")
            print(f"📞 Contact: {center.contact['phone']}")
            print(f"✉️ Email: {center.contact['email']}")
            print(f"📊 Updated resources at {center.name}: {center.resources}")
            
            return True
        else:
            print(f"❌ {center.name} doesn't have sufficient resources!")
            print(f"   Available: {center.resources}")
            print(f"   Required: {resources}")
            return False

    def add_request_from_user(self):
        """Add disaster relief request from user input"""
        from utils.helpers import process_user_allocation_choice
        
        user_input = self.get_user_request()
        if not user_input:
            return
        
        location, resources, urgency = user_input
        
        geocode_data = self.find_nearby_ngos_for_user(location, max_results=5)
        if not geocode_data:
            return
        
        lat, lng, nearby_ngos = geocode_data
        
        self.request_counter += 1
        request_id = f"REQ-{self.request_counter:06d}"
        
        request = DisasterRequest(request_id, location, resources, urgency)
        request.coordinates = (lat, lng)
        
        self.graph.add_node(request_id, lat, lng)
        
        for ngo in nearby_ngos:
            distance = ngo['distance_km']
            self.graph.add_edge(request_id, ngo['id'], distance)
        
        if process_user_allocation_choice(self, request, nearby_ngos, request_id, resources):
            return
        
        heapq.heappush(self.request_queue, request)
        print(f"✅ Request {request_id} added to queue! Urgency level: {urgency}")
        print(f"⏳ There are {len(self.request_queue)} requests pending processing.")

    def find_optimal_relief_center(self, request: DisasterRequest) -> Optional[Tuple[str, float]]:
        """Find nearest relief center with sufficient resources"""
        if not request.coordinates:
            print(f"❌ Cannot process request {request.request_id}: Location not found")
            return None
        
        if request.allocated_center:
            center_id = request.allocated_center
            if center_id in self.relief_centers:
                distance = self.graph.calculate_distance(request.request_id, center_id)
                return (center_id, distance)
        
        return self.routing_service.find_optimal_center(
            request.request_id, self.relief_centers, request.resources
        )

    def process_requests(self):
        """Process requests from priority queue"""
        while self.request_queue and self.is_running:
            request = heapq.heappop(self.request_queue)
            
            print(f"\n🎯 Processing request: {request.request_id} | Urgency: {request.urgency}")
            print(f"📍 Location: {request.location}")
            print(f"📦 Resources needed: {request.resources}")
            
            result = self.find_optimal_relief_center(request)
            
            if result:
                center_id, distance = result
                center = self.relief_centers[center_id]
                
                travel_time = distance / 40
                transport_cost = self.routing_service.calculate_transport_cost(distance, request.resources)
                
                if center.allocate_resources(request.resources):
                    self.allocation_log.add_entry(
                        request.request_id, center_id, request.resources,
                        distance, travel_time, transport_cost,
                        user_selected=bool(request.allocated_center)
                    )
                    
                    print(f"🎉 HELP IS ON THE WAY! 🎉")
                    print(f"✅ Resources allocated from {center.name}")
                    print(f"📦 Resources being sent: {request.resources}")
                    print(f"📍 From: {center.location}")
                    print(f"📏 Distance: {distance:.2f} km")
                    print(f"⏱️ Estimated arrival: {travel_time:.2f} hours")
                    print(f"📞 Contact: {center.contact['phone']}")
                else:
                    print(f"❌ Allocation failed for {request.request_id}")
            else:
                print(f"❌ No suitable relief center found for {request.request_id}")
            
            time.sleep(2)

    def start_system(self):
        """Start the allocation system"""
        self.is_running = True
        print("🚀 DDR-SAS System Started!")
        
        processing_thread = threading.Thread(target=self.process_requests)
        processing_thread.daemon = True
        processing_thread.start()

    def stop_system(self):
        """Stop the allocation system"""
        self.is_running = False
        print("🛑 DDR-SAS System Stopped!")

    def get_system_status(self):
        """Display current system status"""
        print(f"\n📊 SYSTEM STATUS")
        print(f"Pending requests: {len(self.request_queue)}")
        print(f"Active relief centers: {len(self.relief_centers)}")
        print(f"Total allocations logged: {len(self.allocation_log.logs)}")
        
        print(f"\n🏥 Relief Center Resources (Sample):")
        for center_id, center in list(self.relief_centers.items())[:3]:
            print(f"  {center.name}: Food={center.resources['food']}, "
                  f"Water={center.resources['water']}, Medicine={center.resources['medicine']}")
import json
import math
import requests
from typing import Dict, List, Optional, Tuple


class Geocoder:
    def __init__(self, json_file_path: str = "ngos.json"):
        self.nominatim_url = "https://nominatim.openstreetmap.org/search"
        self.headers = {
            'User-Agent': 'DisasterReliefSystem/1.0 (contact@example.com)'
        }
        self.json_file_path = json_file_path
        self.ngos_data = self.load_ngos_data()
    
    def load_ngos_data(self) -> List[Dict]:
        try:
            with open(self.json_file_path, 'r', encoding='utf-8') as file:
                return json.load(file)
        except FileNotFoundError:
            print(f"Error: JSON file '{self.json_file_path}' not found.")
            return []
        except json.JSONDecodeError as e:
            print(f"Error parsing JSON file: {e}")
            return []
    
    def geocode_address(self, address: str) -> Optional[Tuple[float, float, dict]]:
        """Convert address to latitude and longitude using OpenStreetMap Nominatim"""
        params = {
            'q': address,
            'format': 'json',
            'limit': 1
        }
        try:
            response = requests.get(self.nominatim_url, params=params, headers=self.headers, timeout=10)
            response.raise_for_status()
            data = response.json()
            if not data:
                print(f"No results found for address: {address}")
                return None
            location = data[0]
            lat = float(location['lat'])
            lon = float(location['lon'])
            return lat, lon, location
        except requests.exceptions.RequestException as e:
            print(f"Error making API request: {e}")
            return None
    
    def find_nearby_ngos(self, target_lat: float, target_lng: float, max_distance_km: float = 100.0, max_results: int = 5) -> List[Dict]:
        """Find NGOs near the given coordinates using Haversine formula"""
        nearby_ngos = []
        
        for ngo in self.ngos_data:
            ngo_lat = ngo['coordinates']['lat']
            ngo_lng = ngo['coordinates']['lng']
            
            distance = self.haversine_distance(target_lat, target_lng, ngo_lat, ngo_lng)
            
            if distance <= max_distance_km:
                ngo_copy = ngo.copy()
                ngo_copy['distance_km'] = round(distance, 2)
                nearby_ngos.append(ngo_copy)
        
        nearby_ngos.sort(key=lambda x: x['distance_km'])
        return nearby_ngos[:max_results]
    
    def find_nearby_ngos_for_display(self, location: str, max_results: int = 5):
        """Find and display nearby NGOs for a user-provided address"""
        print(f"\n🔍 Searching for location: {location}")
        
        geocode_result = self.geocode_address(location)
        if not geocode_result:
            print("❌ Could not find coordinates for the given address.")
            return None
        
        lat, lng, full_data = geocode_result
        print(f"📍 Coordinates found: {lat:.4f}, {lng:.4f}")
        print(f"📍 Location: {full_data.get('display_name', 'Unknown')}")
        
        print("\n🔄 Searching for nearby relief centers...")
        nearby_ngos = self.find_nearby_ngos(lat, lng, max_distance_km=200.0, max_results=max_results)
        
        if not nearby_ngos:
            print("❌ No relief centers found within 200 km radius.")
            return None
        
        print(f"\n✅ Found {len(nearby_ngos)} relief center(s) nearby:")
        print("="*80)
        
        for i, ngo in enumerate(nearby_ngos, 1):
            print(f"\n{i}. {ngo['name']}")
            print(f"   📍 Location: {ngo['location']}")
            print(f"   📏 Distance: {ngo['distance_km']} km")
            print(f"   📊 Resources - Food: {ngo['resources']['food']}, "
                  f"Water: {ngo['resources']['water']}, "
                  f"Medicine: {ngo['resources']['medicine']}")
            print(f"   📞 Contact: {ngo['contact']['phone']} | {ngo['contact']['email']}")
            print(f"   🆔 ID: {ngo['id']}")
        
        return lat, lng, nearby_ngos
    
    def haversine_distance(self, lat1: float, lng1: float, lat2: float, lng2: float) -> float:
        """Calculate distance between two points using Haversine formula"""
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
    
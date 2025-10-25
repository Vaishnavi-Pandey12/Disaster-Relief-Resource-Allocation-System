from typing import Optional, Tuple, Dict, List, Any


def get_user_input() -> Optional[Tuple[str, Dict[str, int], int]]:
    """Get disaster request details from user"""
    print("\n" + "="*50)
    print("🚨 DISASTER RELIEF REQUEST FORM")
    print("="*50)
    
    location = input("Enter the affected area address: ").strip()
    if not location:
        print("❌ Location is required!")
        return None
    
    print("\n📦 Enter resource requirements:")
    try:
        food = int(input("Food packets needed: ") or 0)
        water = int(input("Water packets needed: ") or 0)
        medicine = int(input("Medicine packets needed: ") or 0)
    except ValueError:
        print("❌ Please enter valid numbers for resources!")
        return None
    
    resources = {
        'food': max(0, food),
        'water': max(0, water),
        'medicine': max(0, medicine)
    }
    
    print("\n🚨 Urgency level:")
    print("1 - Critical (Immediate response needed)")
    print("2 - High (Response within 2 hours)")
    print("3 - Medium (Response within 6 hours)")
    print("4 - Low (Response within 12 hours)")
    print("5 - Minimal (Response within 24 hours)")
    
    try:
        urgency = int(input("Select urgency (1-5): "))
        urgency = max(1, min(5, urgency))
    except ValueError:
        print("❌ Using default urgency level 3")
        urgency = 3
    
    return location, resources, urgency


def process_user_allocation_choice(system, request, nearby_ngos: List[Dict], request_id: str, resources: Dict[str, int]) -> bool:
    """Process user's allocation choice"""
    print(f"\n{'='*60}")
    print("🤔 ALLOCATION OPTIONS:")
    print("1. Let system auto-allocate (most efficient)")
    print("2. Select a specific NGO from the list above")
    
    choice = input("\nChoose allocation method (1 or 2): ").strip()
    
    if choice == '2':
        try:
            ngo_choice = int(input(f"Select NGO (1-{len(nearby_ngos)}): "))
            if 1 <= ngo_choice <= len(nearby_ngos):
                selected_ngo = nearby_ngos[ngo_choice - 1]
                request.allocated_center = selected_ngo['id']
                
                print(f"\n🔄 Allocating to {selected_ngo['name']}...")
                success = system.allocate_to_specific_ngo(request_id, selected_ngo['id'], resources)
                
                if success:
                    print(f"✅ Request {request_id} completed with user-selected NGO!")
                    return True
                else:
                    print("❌ Allocation failed. Adding to queue for auto-allocation...")
            else:
                print("❌ Invalid selection. Using auto-allocation...")
        except ValueError:
            print("❌ Invalid input. Using auto-allocation...")
    
    return False


def display_main_menu():
    """Display the main menu options"""
    print("\n📋 MAIN MENU:")
    print("1. 🚨 Submit Disaster Relief Request")
    print("2. 📊 View System Status")
    print("3. 🔍 Search NGOs by Location (Max 5)")
    print("4. 🛑 Exit System")
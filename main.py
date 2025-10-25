from core.system import DisasterReliefAllocationSystem
from utils.helpers import display_main_menu


def main():
    dras = DisasterReliefAllocationSystem("ngos.json")
    dras.start_system()
    
    print("🌊 DISASTER RELIEF ALLOCATION SYSTEM (DDR-SAS)")
    print("="*55)
    
    while True:
        display_main_menu()
        choice = input("\nEnter your choice (1-4): ").strip()
        
        if choice == '1':
            dras.add_request_from_user()
            
        elif choice == '2':
            dras.get_system_status()
            
        elif choice == '3':
            location = input("Enter location to search for NGOs: ").strip()
            if location:
                dras.find_nearby_ngos_for_user(location, max_results=5)
                
                allocate_now = input("\n❓ Do you want to allocate resources to one of these NGOs? (yes/no): ").strip().lower()
                if allocate_now in ['yes', 'y']:
                    print("\n🚨 Quick Allocation Process:")
                    try:
                        resources = {}
                        resources['food'] = int(input("Food packets needed: ") or 0)
                        resources['water'] = int(input("Water packets needed: ") or 0)
                        resources['medicine'] = int(input("Medicine packets needed: ") or 0)
                        
                        ngo_choice = int(input("Select NGO number (1-5): "))
                        if 1 <= ngo_choice <= 5:
                            print("🔧 Feature: Please use Option 1 for full allocation process")
                        else:
                            print("❌ Invalid NGO selection")
                    except ValueError:
                        print("❌ Invalid input")
            else:
                print("❌ Please enter a valid location.")
                
        elif choice == '4':
            print("\nThank you for using DDR-SAS. Stay safe!")
            dras.stop_system()
            break
            
        else:
            print("❌ Invalid choice. Please enter 1-4.")


if __name__ == "__main__":
    main()
package com.shubhasai.wellnation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HospitaldetailspageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return   when(position){
            0->{
                DepartmentFragment()
            }
            1->{
                ServicesavailableFragment()
            }
            2->{
                DoctorsFragment()
            }
            3->{
                BloodbankFragment()
            }
            4->{
                RoomsFragment()
            }
            else->{
                Fragment()
            }

        }
    }

}
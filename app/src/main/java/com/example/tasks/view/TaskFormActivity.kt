package com.example.tasks.view

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.tasks.R
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.model.TaskModel
import com.example.tasks.viewmodel.TaskFormViewModel
import kotlinx.android.synthetic.main.activity_register.button_save
import kotlinx.android.synthetic.main.activity_task_form.*
import java.text.SimpleDateFormat
import java.util.*

class TaskFormActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var mViewModel: TaskFormViewModel
    private val mDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)                // Define o formato data
    private val mListPriorityId : MutableList<Int> = arrayListOf()
    private var mTaskId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)

        mViewModel = ViewModelProvider(this).get(TaskFormViewModel::class.java)

        // Inicializa eventos
        listeners()
        observe()

        // Traz a lista as prioridades
        mViewModel.listPriorities()

        loadDataFromActivity()
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.button_save) {
            handleSave()
        }
        else if (id == R.id.button_date) {
            showDatePicker()
        }
    }

    private fun showDatePicker() {

        // Pega as informações da data do celular
        val date = Calendar.getInstance()
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH)
        val day = date.get(Calendar.DAY_OF_MONTH)

        // Registra a Data selecionada pelo usuário
        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun loadDataFromActivity(){
        val bundle = intent.extras
        if (bundle != null) {
            mTaskId = bundle.getInt(TaskConstants.BUNDLE.TASKID)
            mViewModel.load(mTaskId)
            button_save.text = getString(R.string.update_task)
        }
    }

    private fun handleSave(){
        val task = TaskModel().apply {
            this.id = mTaskId
            this.description = edit_description.text.toString()
            this.complete = check_complete.isChecked
            this.priorityId = mListPriorityId[spinner_priority.selectedItemPosition]
            this.dueDate = button_date.text.toString()
        }

        mViewModel.save(task)
    }

    private fun observe() {

        // Spinner (Lista de pioridades)
        mViewModel.priorities.observe(this  , androidx.lifecycle.Observer {
            val list: MutableList<String> = arrayListOf()
            for(item in it) {
                list.add(item.description)
                mListPriorityId.add(item.id)
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
            spinner_priority.adapter = adapter
        })


        mViewModel.validation.observe(this, androidx.lifecycle.Observer {
            if(it.successMessage()) {
                //Toast.makeText(this, "Sucesso", Toast.LENGTH_SHORT).show()
                if(mTaskId == 0) {
                    tostMessage(getString(R.string.task_created))
                } else {

                    tostMessage(getString(R.string.task_updated))
                }
                finish()
            }
            else {
                //Toast.makeText(this, it.failureMessage(), Toast.LENGTH_SHORT).show()
                tostMessage(it.failureMessage())
            }
        })


        mViewModel.task.observe(this, androidx.lifecycle.Observer{

            edit_description.setText(it.description)

            check_complete.isChecked = it.complete

            spinner_priority.setSelection(getIndex(it.priorityId))

            val date = SimpleDateFormat("yyyy-MM-dd").parse(it.dueDate)
            button_date.text = mDateFormat.format(date)


        })
    }

    private fun getIndex(priorityId : Int) : Int {
        var index = 0
        for (i in 0 until mListPriorityId.count()) {
            if(mListPriorityId[i] == priorityId){
                index = i
                break
            }
        }
        return index
    }

    private fun listeners() {
        button_save.setOnClickListener(this)                                                        // Botão para adicinar tarefa
        button_date.setOnClickListener(this)                                                        // Botão para definir a data de conclusão da tarefa (Due Date)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = Calendar.getInstance()
        date.set(year, month, dayOfMonth)
        button_date.text = mDateFormat.format(date.time)
    }

    private fun tostMessage(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

}

// Note(Robert): This file is currently being rewritten.

var PointAndClick = PointAndClick || {};

// Fields:
// 1 - possible isomers, the first being the default
// 2 - possible ring types, first is default
// 3 - possible configurations
// 4 - possible linkage positions
// 5 - possible branch/derivative points for pyranose
// 6 - possible branch/derivative points for furanose

var p_1345 = [1, 3, 4, 5];
var p_1346 = [1, 3, 4, 6];
var p_234 = [2, 3, 4];
var p_235 = [2, 3, 5];
var p_2346 = [2, 3, 4, 6];
var p_346 = [3, 4, 6];
var p_4789 = [4, 7, 8, 9];
PointAndClick.residues = {
  "Man": [["D", "L"], ["p"], ["a", "b"], [1], p_2346, []],
  "Gal": [["D", "L"], ["p"], ["a", "b"], [1], p_2346, []],
  "Glc": [["D", "L"], ["p"], ["a", "b"], [1], p_2346, []],
  "Ido": [],
  "All": [["D", "L"], ["p"], ["a", "b"], [1], p_2346, []],
  "Alt": [["D", "L"], ["p"], ["a", "b"], [1], p_2346, []],
  "Gul": [["D", "L"], ["p"], ["a", "b"], [1], p_2346, []],
  "Tal": [["D", "L"], ["p"], ["a", "b"], [1], p_2346, []],
  "Xyl": [["D", "L"], ["p", "f"], ["a", "b"], [1], p_234, p_235],
  "Lyx": [["D", "L"], ["p", "f"], ["a", "b"], [1], p_234, p_235], //?
  "Rib": [["D", "L"], ["p", "f"], ["a", "b"], [1], p_234, p_235],
  "Ara": [["D", "L"], ["p", "f"], ["a", "b"], [1], p_234, p_235],
  "Fru": [["D", "L"], ["p", "f"], ["a", "b"], [2], p_1345, p_1346],
  "Psi": [["D", "L"], ["p", "f"], ["a", "b"], [2], p_1345, p_1346],
  "Sor": [["D", "L"], ["p", "f"], ["a", "b"], [2], p_1345, p_1346],
  "Tag": [["D", "L"], ["p", "f"], ["a", "b"], [2], p_1345, p_1346],
  "Fuc": [["L", "D"], ["p"], ["a", "b"], [1], p_234, []],
  "Rha": [["L", "D"], ["p"], ["a", "b"], [1], p_234, []],
  "Qui": [["D", "L"], ["p"], ["a", "b"], [1], p_234, []],
  "GalNAc": [["D", "L"], ["p"], ["a", "b"], [1], p_346, []],
  "GlcNAc": [["D", "L"], ["p"], ["a", "b"], [1], p_346, []],
  "ManNAc": [["D", "L"], ["p"], ["a", "b"], [1], p_346, []],
  "GalA": [["D", "L"], ["p"], ["a", "b"], [1], p_234, []],  
  "GlcA": [["D", "L"], ["p"], ["a", "b"], [1], p_234, []],
  "IdoA": [["D", "L"], ["p"], ["a", "b"], [1], p_234, []],
  "Neu5Ac": [["D"], ["p"], ["a"], [2], p_4789, []],  
  "KDN": [],
  "KDO": [],
  "Neu5Gc": [["D"], ["p"], ["a"], [2], p_4789, []]
};

PointAndClick.derivatives = {
  "Sulfate": ["S"],
  "Phosphate": ["P"],
  "Acetyl": ["A"],
  "Methyl": ["Me"]
};

PointAndClick.row_sizes = [8, 8, 3, 3, 3, 4];

PointAndClick.terminals = ["OH", "OME", "OtBu"];

PointAndClick.disabled_color = "#999999";
PointAndClick.enable_color = "#FFFFFF";


PointAndClick.is_branching = false;
PointAndClick.main_chain = "";
PointAndClick.current_branch_index = 0;

PointAndClick.init = function() {
  PointAndClick.sequence = document.getElementById("sequence");
  PointAndClick.sequence.value = "";
  PointAndClick.sequence_array = [];
  PointAndClick.init_residues();
  PointAndClick.init_isomers();
  PointAndClick.init_configurations();
  PointAndClick.init_ringtypes();
  PointAndClick.init_terminals();
  PointAndClick.init_linkages();
  PointAndClick.init_derivatives();
  PointAndClick.init_positions();
  PointAndClick.to_state_1();
}

PointAndClick.init_residues = function() {
  var count = 0;
  var current_row_index = 0;
  for (var i in PointAndClick.residues) {
    try{
      var child = document.createElement("input");
      child.type = "button";
      child.value = i;
      child.onclick = function() { 
        PointAndClick.residue_click(this.value); 
      };
      if (count == PointAndClick.row_sizes[current_row_index]) {
          count = 0;
          document.getElementById("residues").appendChild(
            document.createElement("br")
          );
          current_row_index++;
      }
      count++;
      document.getElementById("residues").appendChild(child);
    } catch(err) {}
  }
  PointAndClick.enable_residues();
}

PointAndClick.init_isomers = function() {
  document.getElementById("L").onclick = function() { 
    PointAndClick.isomer_click('L'); 
  };
  document.getElementById("D").onclick = function() { 
    PointAndClick.isomer_click('D'); 
  };
  PointAndClick.enable_isomers();
}

PointAndClick.init_configurations = function() {
  document.getElementById("alpha").onclick = function() { 
    PointAndClick.configuration_click('a'); 
  };
  document.getElementById("beta").onclick = function() { 
    PointAndClick.configuration_click('b'); 
  };
  PointAndClick.disable_configurations();
}

PointAndClick.init_ringtypes = function() {
  document.getElementById("furanose").onclick = function() { 
    PointAndClick.ringtype_click('f'); 
  };
  document.getElementById("pyranose").onclick = function() { 
    PointAndClick.ringtype_click('p'); 
  };
  PointAndClick.disable_ringtypes();
}

PointAndClick.init_terminals = function() {
    for (var j = 0; j < PointAndClick.terminals.length; j++) {
      var child = document.createElement("input");
      child.type = "button";
      child.value = "-" + PointAndClick.terminals[j];
      try{
        child.onclick = function() { 
          PointAndClick.terminal_click(this.value); 
        };
      } catch(err) {}
      document.getElementById("terminals").appendChild(child);
    }
  PointAndClick.disable_terminals();
}

PointAndClick.init_linkages = function() {
  for (var i = 1; i <= 9; i++) {
    try{
      var child1 = document.createElement("input");
      var child2 = document.createElement("input");
      child1.type = "button";
      child2.type = "button";
      child1.value = "1-" + i;
      child2.value = "2-" + i;
      child1.id = "linkage_1" + i;
      child2.id = "linkage_2" + i;
      child1.onclick = function() { 
        PointAndClick.linkage_click(this.value); 
      };
      child2.onclick = function() {
        PointAndClick.linkage_click(this.value);
      }
      document.getElementById("one_linkages").appendChild(child1);
      document.getElementById("two_linkages").appendChild(child2);
    } catch(err) {}
  }
  PointAndClick.disable_linkages();
}

PointAndClick.init_derivatives = function() {
  for (var i in PointAndClick.derivatives) {
    try {
      var child = document.createElement("input");
      child.type = "button";
      child.value = i;
      child.onclick = function() {
        PointAndClick.derivative_click(PointAndClick.derivatives[this.value][0]);
      }
      document.getElementById("derivatives").appendChild(child);
    } catch(err) {}
  }
}

PointAndClick.init_positions = function() {
  var allDiv = document.createElement("div");
  allDiv.style.display = "inline-block";
  allDiv.innerHTML = "All" + "<br />";
  var allBox = document.createElement("input");
  allBox.id = "position_all";
  allBox.name = "position_all";
  allBox.value = "All";
  allBox.type = "checkbox";
  allBox.onclick = function() {
    for (var i = 1; i <= 9; i++) {
      var position = document.getElementById("position_" + i);
      if (!position.disabled && position.checked !== this.checked) {
        position.checked = this.checked;
        PointAndClick.position_click(i, this.checked);
      }
    }
  }
  allDiv.appendChild(allBox);
  document.getElementById("positions").appendChild(allDiv);
  for (var i = 1; i <= 9; i++) {
    var child = document.createElement("div");
    child.style.display = "inline-block";
    child.innerHTML = i + "<br />";
    var box = document.createElement("input");
    box.id = "position_" + i;
    box.name = "position_" + i;
    box.type = "checkbox";
    box.value = i;
    box.onclick = function() {
      PointAndClick.position_click(this.value, this.checked);
    }
    box.disabled = "true";
    child.appendChild(box);
    document.getElementById("positions").appendChild(child);
  }
}

PointAndClick.set_direction = function(str) {
  document.getElementById("direction").innerHTML = str;
}

PointAndClick.disable_all = function() {
  PointAndClick.disable_residues();
  PointAndClick.disable_terminals();
  PointAndClick.disable_linkages();
  PointAndClick.disable_isomers();
  PointAndClick.disable_configurations();
  PointAndClick.disable_ringtypes();
  PointAndClick.disable_add_branch();
  PointAndClick.disable_buttons("derivatives");
  PointAndClick.disable("continue");
  PointAndClick.disable("finish_branch");
  PointAndClick.disable("add_derivative");
  PointAndClick.disable_buttons("positions");
  PointAndClick.disable("apply_derivatives");
}

PointAndClick.to_state_1 = function() {
  var sequence_array = PointAndClick.sequence_array;
  var sequence = PointAndClick.sequence.value || "";

  PointAndClick.set_direction("Select an isomer or select a monosaccharide and the default isomer will be chosen.");
  PointAndClick.disable_all();
  PointAndClick.enable_isomers();

  if (sequence.length > 0) {
    var position = parseInt(sequence[sequence.length - 1], 10);
    PointAndClick.enable_residues_by_position(position);
  } else {
    PointAndClick.enable_residues();
  }

  PointAndClick.enable("undo");
  PointAndClick.enable("clear");
  if (PointAndClick.is_branching && sequence_array.length > 0) {
    var linkage = sequence_array[sequence_array.length - 1];
    var linkage_pos = linkage[linkage.length - 1];
    var used_positions = PointAndClick.get_used_positions(
            PointAndClick.current_branch_index, PointAndClick.main_chain);
    var show_finish_branch = true;
    for (var i = 0; i < used_positions.length; i++) {
       if (used_positions[i] == linkage_pos)
           show_finish_branch = false;
    }
    if (show_finish_branch)
        PointAndClick.enable("finish_branch");
  }
  PointAndClick.current_state = 1;
}

PointAndClick.to_state_2 = function() {
  PointAndClick.disable_all();
  var isomer = 
    PointAndClick.sequence_array[PointAndClick.sequence_array.length - 1];
  
  var sequence = PointAndClick.sequence.value || "";
  if (sequence.length > 1) {
    var position = parseInt(sequence[sequence.length - 2], 10);
    PointAndClick.enable_residues_by_position_and_isomer(position, isomer);
  } else  {
    PointAndClick.enable_residues_by_isomer(isomer);
  }

  //PointAndClick.enable_residues_by_isomer(isomer);
  PointAndClick.set_direction("Select a monosaccharide.");
  PointAndClick.current_state = 2;
}

PointAndClick.to_state_3 = function() {
  PointAndClick.disable_all();
  
  var sequence_array_length = PointAndClick.sequence_array.length,
      residue = PointAndClick.sequence_array[sequence_array_length - 1],
      prev_linkage = PointAndClick.sequence_array[sequence_array_length - 3];
  if (PointAndClick.residues[residue][1].length == 1) {
    PointAndClick.ringtype_click(PointAndClick.residues[residue][1][0]);
    return;
  }
  if (prev_linkage !== undefined) {
    var position = parseInt(prev_linkage[prev_linkage.length - 1], 10);
    var pyranose_positions = PointAndClick.residues[residue][4];
    var pyranose_possible = false;
    var furanose_possible = false;
    for (var i = 0; i < pyranose_positions.length; i++) {
      if (pyranose_positions[i] == position) {
        pyranose_possible = true;
        break;
      }
    }
    var furanose_positions = PointAndClick.residues[residue][5];
    for (var i = 0; i < furanose_positions.length; i++) {
      if (furanose_positions[i] == position) {
        furanose_possible = true;
        break;
      }
    }
    if (pyranose_possible && !furanose_possible) {
      PointAndClick.ringtype_click('p');
      return;
    } else if (furanose_possible && !pyranose_possible) {
      PointAndClick.ringtype_click('f');
      return;
    }
  }

  PointAndClick.enable_ringtypes();
  PointAndClick.enable_configurations();
  //PointAndClick.enable_ringtypes();
  PointAndClick.set_direction("Select a ring type or select a configuration and the default ring type will be chosen.");
  PointAndClick.current_state = 3;
}

PointAndClick.to_state_4 = function() {
  var residue =
    PointAndClick.sequence_array[PointAndClick.sequence_array.length - 1];
  var without_ringtype = residue.slice(0, 3);
  if (residue.length > 4)
    without_ringtype += residue.slice(4);
  if (PointAndClick.residues[without_ringtype][2].length == 1) {
    PointAndClick.configuration_click(
      PointAndClick.residues[without_ringtype][2][0]
    );
    return;
  }  
  PointAndClick.set_direction("Select a configuration.");
  PointAndClick.disable_all();
  PointAndClick.enable_configurations();
  PointAndClick.current_state = 4;
}

PointAndClick.to_state_5 = function() {
  PointAndClick.disable_all();
  if (!PointAndClick.is_branching) {
    //PointAndClick.enable_terminals();
    PointAndClick.set_direction("Select a linkage or an aglycon.");
  }
  else {
    PointAndClick.set_direction("Select a linkage.");
  }

  var sequence_array = PointAndClick.sequence_array;
  //if (sequence_array.length > 3) {
  //  var prev_linkage = sequence_array[sequence_array.length - 4];
  //  var prev_linkage_pos = prev_linkage[prev_linkage.length - 1];
    //alert(prev_linkage_pos);
  //}

  var residue = PointAndClick.sequence_array[
                              PointAndClick.sequence_array.length - 2
                              ];
  var without_ringtype = residue.slice(0, 3);
  if (residue.length > 4)
    without_ringtype += residue.slice(4);

/*
  var c_positions = PointAndClick.residues[without_ringtype][3] || [];
  var o_positions = PointAndClick.residues[without_ringtype][4] || [];

  for (var i = 0; i < c_positions.length; i++) {
    for (var j = 0; j < o_positions.length; j++) {
      PointAndClick.enable("linkage_" + c_positions[i] + o_positions[j]);
    }
  }
*/
  
  for (var i = 0; i < PointAndClick.residues[without_ringtype][3].length; i++) {
    var position = PointAndClick.residues[without_ringtype][3][i];
    switch (position) {
     case 1:
      PointAndClick.enable_buttons("one_linkages");
      break;
     case 2:
      PointAndClick.enable_buttons("two_linkages");
      break;
    }
  }
    if (!PointAndClick.is_branching)
      PointAndClick.enable_buttons("terminals");


  PointAndClick.current_state = 5;
}

PointAndClick.to_state_6 = function() {
  PointAndClick.disable_all();
  PointAndClick.show("add_branch", "inline");
  PointAndClick.show("add_derivative", "inline");
  PointAndClick.enable("add_derivative");
  PointAndClick.enable("add_branch");
  //if (!PointAndClick.is_branching)
    PointAndClick.enable("continue");
  PointAndClick.set_direction("Add a branch or derivative or select \"Done\" to complete your sequence");
  PointAndClick.current_state = 6;
}

PointAndClick.to_state_7 = function() {
  PointAndClick.disable_all();
  PointAndClick.hide("add_branch");
  PointAndClick.hide("add_derivative");
  PointAndClick.set_direction("Select a residue to branch from.");
  PointAndClick.current_state = 7;
}

PointAndClick.to_state_8 = function() {
  PointAndClick.disable_all();
  PointAndClick.hide("add_branch");
  PointAndClick.hide("add_derivative");
  PointAndClick.show("apply_derivatives", "inline");
  PointAndClick.set_direction("Select a residue to add a derivative to.");
  PointAndClick.current_state = 8;
}

PointAndClick.to_state_9 = function() {
  PointAndClick.disable_all();
  PointAndClick.enable_buttons("derivatives");
  // Remove the below when phosphate is available.
  var allInput = document.getElementsByTagName("input");
  for (var i = 0; i < allInput.length; i++)
    if (allInput[i].value == "Phosphate")
      allInput[i].disabled = true;
  PointAndClick.set_direction("Select a derivative.");
  PointAndClick.current_state = 9;
}

PointAndClick.to_state_10 = function() {
  PointAndClick.set_direction("Select the positions for the derivative.");
  PointAndClick.show("position_section", "block");
  PointAndClick.enable_buttons("positions");

  for (var i = 1; i <= 9; i++) {
    PointAndClick.disable("position_" + i);
  }

  var index = PointAndClick.current_derivative_index;
  index--;
  var last = index;
  var sequence = PointAndClick.sequence.value;
  while (true) {
    if (index < 0) {
      index++;
      break;
    } else if (sequence[index] == '[' || sequence[index] == ']') {
      index++;
      break;
    } else if (sequence[index] == '-') {
      index += 2;
      break;
    }
    index--;
  }
  var first = index + 1;

  var sugar = sequence.slice(first, last + 1);
  if (sugar.length > 3 && (sugar[3] === 'p' || sugar[3] === 'f')) {
    sugar = sugar.substring(0, 3) + sugar.substring(4);
  }

  var spots = PointAndClick.residues[sugar][4];
  for (var i = 0; i < spots.length; i++) {
    PointAndClick.enable("position_" + spots[i]);
  }

  // I don't think this should be necessary.
  var anomeric = PointAndClick.residues[sugar][3];
  for (var i = 0; i < anomeric.length; i++) {
    PointAndClick.disable("position_" + anomeric[i]);
  }

  var used_positions = PointAndClick.get_used_positions(index);
  for (var i = 0; i < used_positions.length; i++) {
    PointAndClick.disable("position_" + used_positions[i]);
  }

  var derivatives = PointAndClick.current_derivatives.split(",");
  for (var i = 0; i < derivatives.length; i++) {
    var position = derivatives[i][0];
    if (position !== undefined) {
      document.getElementById("position_" + position).checked = "checked";
    }
  }
 
  PointAndClick.enable("apply_derivatives");
  PointAndClick.hide("derivative_section");
  PointAndClick.current_state = 10;
}

PointAndClick.get_used_positions = function(index, str) {
  var positions = [],
      sequence = str || PointAndClick.sequence.value;

  if (index <= 0) {
    return positions;
  }

  index--;
  while (sequence[index] == ']') {
    positions.push(sequence[index - 1]);
    var count = 1;
    while (count > 0) {
      index--;
      if (sequence[index] == ']') {
        count++;
      } else if (sequence[index] == '[') {
        count--;
      }
    }
    // index points to the matching [

    index--;
  }
 
  if (sequence[index] !== '[') {
    positions.push(sequence[index]);
  }

  return positions;
}

PointAndClick.update_sequence = function() {
  var sequence = "";
  for (var i = 0; i < PointAndClick.sequence_array.length;  i++) {
    sequence += PointAndClick.sequence_array[i];
  }

  if (PointAndClick.is_branching == false) {
    PointAndClick.sequence.value = sequence;
  }
  else {
    var beginning = 
      PointAndClick.main_chain.slice(0, PointAndClick.current_branch_index);
    var end = 
      PointAndClick.main_chain.slice(PointAndClick.current_branch_index);
    document.getElementById("current_sequence").innerHTML =
      "<span style=\"color:#999999\">" + beginning + "</span>" + 
      "[" + sequence + "]" + "<span style=\"color:#999999\">" + end +
      "</span>";

    PointAndClick.sequence.value = sequence;
  }
}

PointAndClick.add_to_sequence = function(string) {
  PointAndClick.sequence_array.push(string);
  PointAndClick.update_sequence();
}

PointAndClick.remove_from_sequence = function() {
  PointAndClick.sequence_array.pop();
  PointAndClick.update_sequence();
}

PointAndClick.undo = function() {
  if (PointAndClick.sequence_array.length == 0)
    return;
  PointAndClick.remove_from_sequence();
  switch (PointAndClick.current_state) {
   case 1:
    PointAndClick.to_state_5();
    break;
   case 2:
    PointAndClick.to_state_1();
    break;
   case 3:
    PointAndClick.to_state_2();
    break;
   case 4:
    PointAndClick.to_state_2();
    break;
   case 5:
    PointAndClick.to_state_4();
    break;
   case 6:
    PointAndClick.to_state_5();
    break;
   case 7:
    document.getElementById("branch_options").style.display = "none";
    PointAndClick.to_state_6();
    break;
  }
}

PointAndClick.clear = function() {
  PointAndClick.sequence_array.length = 0;
  PointAndClick.update_sequence();
  PointAndClick.to_state_1();
}

PointAndClick.insert_into_string = function(string1, string2, index) {
  var beginning = string1.slice(0, index);
  var end = string1.slice(index);
  return beginning + string2 + end;
}

PointAndClick.enable_button = function(button) {
  try {
    button.disabled = false;
    button.className = "";
  }
  catch(err) {}
}

PointAndClick.disable_button = function(button) {
  try {
    button.className = "disabled";
    button.disabled = true;
  }
  catch(err) {}
}

PointAndClick.enable = function(id) {
  PointAndClick.enable_button(document.getElementById(id));
}

PointAndClick.disable = function(id) {
  PointAndClick.disable_button(document.getElementById(id));
}

PointAndClick.hide_element = function(element) {
  element.style.display = "none";
}

PointAndClick.show = function(id, display) {
  document.getElementById(id).style.display = display;
}

PointAndClick.hide = function(id) {
  PointAndClick.hide_element(document.getElementById(id));
}

PointAndClick.residue_click = function(residue) {
  if (PointAndClick.current_state == 1) {
    PointAndClick.add_to_sequence(PointAndClick.residues[residue][0][0]);
  }
  PointAndClick.add_to_sequence(residue);
  PointAndClick.to_state_3();
}

PointAndClick.enable_residues_by_isomer = function(isomer) {
  var buttons =
    document.getElementById("residues").getElementsByTagName("input");
  for (i in buttons) {
    var residue = buttons[i].value;
    if (PointAndClick.residues[residue] != undefined &&
        PointAndClick.residues[residue].length > 0) {
      for (var j = 0; j < PointAndClick.residues[residue][0].length; j++) {
        if (PointAndClick.residues[residue][0][j] == isomer) {
          PointAndClick.enable_button(buttons[i]);
          break;
        }
      }
    }
  }
}

PointAndClick.enable_residues_by_position = function(position) {
  var buttons = document.getElementById("residues").getElementsByTagName("input"),
      i, j, value, table_row;
  for (i = 0; i < buttons.length; i++) {
    value = buttons[i].value;
    table_row = PointAndClick.residues[value];
    if (table_row !== undefined && table_row.length > 0) {
      var all_positions = table_row[4].concat(table_row[5]);
      for (j = 0; j < all_positions.length; j++) {
        if (all_positions[j] === position) {
          PointAndClick.enable_button(buttons[i]);
          break;
        }
      }
    }
  }
}

PointAndClick.enable_residues_by_position_and_isomer = function(position, isomer) {
  var buttons = document.getElementById("residues").getElementsByTagName("input"),
      i, j, value, table_row;
  for (i = 0; i < buttons.length; i++) {
    value = buttons[i].value;
    table_row = PointAndClick.residues[value];
    if (table_row !== undefined && table_row.length > 0) {
      var isomers = table_row[0];
      var isomerFound = false;
      for (j = 0; j < isomers.length; j++) {
        if (isomers[j] === isomer) {
          isomerFound = true;
          break;
        }
      }
      if (!isomerFound)
        continue;

      var all_positions = table_row[4].concat(table_row[5]);
      for (j = 0; j < all_positions.length; j++) {
        if (all_positions[j] === position) {
          PointAndClick.enable_button(buttons[i]);
          break;
        }
      }
    }
  }
}

PointAndClick.enable_residues = function() {
  var buttons = 
    document.getElementById("residues").getElementsByTagName("input");
  for (i in buttons) {
    if (PointAndClick.residues[buttons[i].value] != undefined &&
        PointAndClick.residues[buttons[i].value].length > 0)
      PointAndClick.enable_button(buttons[i]);
  }
}

PointAndClick.disable_residues = function() {
  PointAndClick.disable_buttons("residues");
}

PointAndClick.terminal_click = function(terminal) {
  var residue = PointAndClick.sequence_array[
                              PointAndClick.sequence_array.length - 2
                              ];
  var without_ringtype = residue.slice(0, 3);
  if (residue.length > 4)
    without_ringtype += residue.slice(4);
  var carbon_index = PointAndClick.residues[without_ringtype][3][0]
  PointAndClick.add_to_sequence(carbon_index + terminal);
  PointAndClick.hide("terminal_section");
  PointAndClick.show("derivative_section", "block");
  PointAndClick.to_state_6();
}

PointAndClick.enable_terminals = function() {
  PointAndClick.enable_buttons("terminals");
}

PointAndClick.disable_terminals = function() {
  PointAndClick.disable_buttons("terminals");
}

PointAndClick.linkage_click = function(linkage) {
  PointAndClick.add_to_sequence(linkage);
  PointAndClick.to_state_1();
}

PointAndClick.enable_linkages = function() {
  PointAndClick.enable_buttons("linkages");
}

PointAndClick.disable_linkages = function() {
  PointAndClick.disable_buttons("linkages");
}

PointAndClick.isomer_click = function(isomer) {
  PointAndClick.add_to_sequence(isomer);
  PointAndClick.to_state_2();
}

PointAndClick.enable_isomers = function() {
  PointAndClick.enable("L");
  PointAndClick.enable("D");
}

PointAndClick.disable_isomers = function() {
  PointAndClick.disable("L");
  PointAndClick.disable("D");
}

PointAndClick.configuration_click = function(configuration) {
  if (PointAndClick.current_state == 3) {
    var residue = PointAndClick.sequence_array.pop();
    var modified_residue = PointAndClick.insert_into_string(
       residue, PointAndClick.residues[residue][1][0], 3
    );
    PointAndClick.add_to_sequence(modified_residue);
  }
  PointAndClick.add_to_sequence(configuration);
  PointAndClick.to_state_5();
}

PointAndClick.enable_configurations = function() {
  PointAndClick.enable("alpha");
  PointAndClick.enable("beta");
}

PointAndClick.disable_configurations = function() {
  PointAndClick.disable("alpha");
  PointAndClick.disable("beta");
}

PointAndClick.ringtype_click = function(ringtype) {
    var residue = PointAndClick.sequence_array.pop();
    var modified_residue = PointAndClick.insert_into_string(residue, ringtype, 3);
    PointAndClick.add_to_sequence(modified_residue);
  PointAndClick.to_state_4();
}

PointAndClick.enable_ringtypes = function() {
  PointAndClick.enable("furanose");
  PointAndClick.enable("pyranose");
}

PointAndClick.disable_ringtypes = function() {
  PointAndClick.disable("furanose");
  PointAndClick.disable("pyranose");
}

PointAndClick.show_branch_options = function(callback) {
  var text = PointAndClick.sequence.value;
  var pattern = /([a-zA-Z][\[\],a-zA-Z0-9]+[ab])[0-9]-/g;
  var branch_options = document.getElementById("branch_options");
  var new_text = "";
  var array_ret;
  var prev_last_index = 0;
  while ((array_ret = pattern.exec(text)) != null) {
    var first_index = pattern.lastIndex - array_ret[0].length;
    new_text += "<span>" + 
                 text.slice(prev_last_index, first_index).
                      replace(/\[/g, "<strong>[</strong>").
                      replace(/\]/g, "<strong>]</strong>") + 
                "</span>";
    new_text += "<span class=\"branch_residue\"" +
                    " onmouseover=\"this.style.cursor=\'default\';" +
                                    "this.style.background = \'#60AFFE\'\"";
    new_text +=     " onmouseout=\"this.style.background = \'white\'\" " +
                    " style=\"display:inline-block\"";
    new_text +=     " onclick=\"" + callback + "(" + first_index + ");\">";
    new_text +=     array_ret[1];
    new_text += "</span>";
    prev_last_index = first_index + array_ret[1].length;
  }
  new_text += text.slice(prev_last_index);

  branch_options.innerHTML = new_text;
  document.getElementById("branch_options").style.display = "block";
}

PointAndClick.add_branch_click = function() {
  PointAndClick.show_branch_options("PointAndClick.branch_at");
  PointAndClick.to_state_7();
}

PointAndClick.add_derivative_click = function() {
  PointAndClick.show_branch_options("PointAndClick.derivative_at");
  PointAndClick.to_state_8();
}

PointAndClick.derivative_click = function(derivative) {
  document.getElementById("apply_derivatives").onclick = function() {
    PointAndClick.apply_derivatives_click(derivative);
  }
  PointAndClick.current_derivative = derivative;
  PointAndClick.to_state_10();
}

PointAndClick.position_click = function(position, checked) {
  var derivative_name = position + PointAndClick.current_derivative;
  if (checked) {
    if (PointAndClick.current_derivatives != "")
      PointAndClick.current_derivatives += ",";
    PointAndClick.current_derivatives += derivative_name;
  }
  else {
    var derivatives = PointAndClick.current_derivatives;
    if (derivatives.slice(0, derivative_name.length + 1) == derivative_name + ",") {
      PointAndClick.current_derivatives = 
        derivatives.slice(derivative_name.length + 1);
    }
    PointAndClick.current_derivatives =
      PointAndClick.current_derivatives.replace("," + derivative_name, "");
    PointAndClick.current_derivatives =
      PointAndClick.current_derivatives.replace(derivative_name, "");
  }
  PointAndClick.update_derivatives(true);
}

//the boolean argument tells whether to show the brackets if the
//current derivatives list is empty
PointAndClick.update_derivatives = function(show_brackets_if_empty) {
  
  var is_empty = PointAndClick.current_derivatives.length == 0;
  var show_brackets = show_brackets_if_empty || !is_empty;
  var beginning =
    PointAndClick.main_chain.slice(0, PointAndClick.current_derivative_index);
  var end =
    PointAndClick.main_chain.slice(PointAndClick.current_derivative_index);
  PointAndClick.sequence.value = beginning + 
                                 ((show_brackets)?"[":"") + 
                                 PointAndClick.current_derivatives + 
                                 ((show_brackets)?"]":"") + 
                                 end;
}

PointAndClick.derivative_at = function(index) {
  //find position in string
  var current_sequence = PointAndClick.sequence.value;
  while (current_sequence[index] != '-')
    index++;
  index -= 2;
  if (current_sequence[index - 1] == ']') {
    var end_index = index;
    while (current_sequence[index] != '[')
      index--;
    PointAndClick.current_derivatives = current_sequence.slice(index + 1, end_index - 1);
    PointAndClick.main_chain = current_sequence.slice(0, index) +
                               current_sequence.slice(end_index);
  }
  else {
    PointAndClick.main_chain = current_sequence;
    PointAndClick.current_derivatives = "";
  }
  
  PointAndClick.current_derivative_index = index;
  PointAndClick.update_derivatives(true);
  PointAndClick.hide("branch_options");
  PointAndClick.to_state_9();
}

PointAndClick.apply_derivatives_click = function(derivative) {
  PointAndClick.update_derivatives(false);

  PointAndClick.hide("position_section");
  PointAndClick.show("derivative_section", "block");
  PointAndClick.hide("apply_derivatives");
  PointAndClick.uncheck_positions();
  PointAndClick.to_state_6();
}

PointAndClick.uncheck_positions = function() {
  var boxes = document.getElementById("positions").getElementsByTagName("input");
  for (i in boxes) {
    boxes[i].checked = false;
  } 
}

PointAndClick.branch_at = function(index) {
  document.getElementById("branch_options").style.display = "none";
  PointAndClick.is_branching = true;
  PointAndClick.current_branch_index = index;
  PointAndClick.main_chain = PointAndClick.sequence.value;
  PointAndClick.sequence_array.length = 0;
  document.getElementById("current_sequence").style.display = "block";
  PointAndClick.update_sequence();
  document.getElementById("finish_branch").style.display = "inline";
  PointAndClick.disable("finish_branch");
  PointAndClick.to_state_1();
}

PointAndClick.finish_branch_click = function() {
  document.getElementById("finish_branch").style.display = "none";
  var sequence = "";
  for (var i = 0; i < PointAndClick.sequence_array.length;  i++) {
    sequence += PointAndClick.sequence_array[i];
  }
  var beginning = 
    PointAndClick.main_chain.slice(0, PointAndClick.current_branch_index);
  var end = 
    PointAndClick.main_chain.slice(PointAndClick.current_branch_index);
  PointAndClick.sequence.value = beginning + "[" + sequence + "]" + end;
  PointAndClick.sequence_array.length = 0;
  PointAndClick.main_chain = PointAndClick.sequence.value;
  PointAndClick.is_branching = false;
  document.getElementById("current_sequence").style.display = "none";
  PointAndClick.to_state_6();
}

PointAndClick.enable_add_branch = function() {
  PointAndClick.enable("add_branch");
}

PointAndClick.disable_add_branch = function() {
  PointAndClick.disable("add_branch");
  
}

var returnVal = "";

PointAndClick.done_click = function() {
  returnVal = PointAndClick.sequence.value;
  window.top.hidePopWin(true);
}

PointAndClick.enable_buttons = function(id) {
  var buttons = document.getElementById(id).getElementsByTagName("input");
  for (i in buttons) {
    PointAndClick.enable_button(buttons[i]);
  }
}

PointAndClick.disable_buttons = function(id) {
  var buttons = document.getElementById(id).getElementsByTagName("input");
  for (i in buttons) {
    PointAndClick.disable_button(buttons[i]);
  }
}

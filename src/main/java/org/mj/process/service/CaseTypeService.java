package org.mj.process.service;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.Factory;
import com.filenet.api.util.Id;
import com.ibm.casemgmt.api.Case;
import com.ibm.casemgmt.api.CaseType;
import com.ibm.casemgmt.api.DeployedSolution;
import com.ibm.casemgmt.api.context.CaseMgmtContext;
import com.ibm.casemgmt.api.context.SimpleP8ConnectionCache;
import com.ibm.casemgmt.api.context.SimpleVWSessionCache;
import com.ibm.casemgmt.api.objectref.ObjectStoreReference;
import com.ibm.casemgmt.api.properties.CaseMgmtProperty;
import com.ibm.mj.core.ceObject.PropertiesTool;
import lombok.Data;
import org.mj.process.model.Attribute;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Data
public class CaseTypeService {

    private static Logger logger = Logger.getLogger(CaseTypeService.class.getName());
    ServerConfig serverConfig;
    ObjectStoreReference objectStoreReference;

    public CaseTypeService(ServerConfig config) {
        serverConfig = config;
        objectStoreReference = new ObjectStoreReference(serverConfig.getOs());
        SimpleVWSessionCache vwSessCache = new SimpleVWSessionCache();
        CaseMgmtContext cmc = new CaseMgmtContext(vwSessCache, new SimpleP8ConnectionCache());
        CaseMgmtContext.set(cmc);
    }

    public List<Attribute> getAttributesForCaseTypes(Set<String> caseTypes) {
        List<Attribute> attributes = new ArrayList<>();
        for (String caseType : caseTypes) {
            logger.info("Case Type info " + caseType);
            CaseType caseT = getCaseType(caseType);
            if (caseT != null) {
                logger.info("compare : " + caseT.getId().toString() + " == " + caseType);
                if (caseT.getId().toString().equals(caseType)) {
                    attributes.add(new Attribute(caseT.getDisplayName(), caseType));
                }
            }
        }
        logger.info("The number of caseAttribute " + attributes.size());
        return attributes;
    }

    public List<Attribute> getAttributesForCaseSolution(Set<String> caseTypes) {
        List<Attribute> attributes = new ArrayList<>();
        for (String caseType : caseTypes) {
            logger.info("Case Type info " + caseType);
            CaseType caseT = getCaseType(caseType);
            if (caseT != null) {
                if (caseT.getId().toString().equals(caseType)) {
                    logger.info("Case Type : " + caseT.getName() + " from the solution " + caseT.getSolutionName());
                    attributes.add(new Attribute(caseT.getSolutionName(), caseT.getSolutionName()));
                }
            }
        }
        logger.info("The number of caseAttribute " + attributes.size());
        return attributes;
    }


    public List<DeployedSolution> getSolutions() {
        String[] objectsStore = new String[1];
        objectsStore[0] = serverConfig.getOs().get_SymbolicName();
        List<DeployedSolution> solutions = DeployedSolution.fetchSolutions(serverConfig.getConnectionTool().getConnection().getURI(), objectsStore);
        logger.info("Retrieved the solutions" + solutions.size() + " Solutions");
        return solutions;
    }

    public List<Attribute> getAttributesSolutions() {
        String[] objectsStore = new String[1];
        objectsStore[0] = serverConfig.getOs().get_SymbolicName();
        List<Attribute> attributes = new ArrayList<>();
        List<DeployedSolution> solutions = DeployedSolution.fetchSolutions(serverConfig.getConnectionTool().getConnection().getURI(), objectsStore);
        logger.info("Retrieved the solutions" + solutions.size() + " Solutions");
        for (DeployedSolution solution : solutions) {
            attributes.add(new Attribute(solution.getSolutionName(), solution.getSolutionName()));
        }
        return attributes;
    }

    public List<Attribute> getAttributesCaseType(String solutionName) {
        logger.info("Retrieved the case type for a " + solutionName);
        List<Attribute> attributes = new ArrayList<>();
        DeployedSolution solution = DeployedSolution.fetchInstance(objectStoreReference, solutionName);
        for (CaseType caseType : solution.getCaseTypes()) {
            attributes.add(new Attribute(caseType.getDisplayName(), caseType.getName()));
        }
        return attributes;
    }

    public List<Attribute> getAttributesIDCaseType(String solutionName) {
        logger.info("Retrieved the case type for a " + solutionName);
        List<Attribute> attributes = new ArrayList<>();
        DeployedSolution solution = DeployedSolution.fetchInstance(objectStoreReference, solutionName);
        for (CaseType caseType : solution.getCaseTypes()) {
            attributes.add(new Attribute(caseType.getDisplayName(), caseType.getId().toString()));
        }
        return attributes;
    }

    public List<Attribute> getAttributesCaseType(String solutionName, Set<String> types) {
        logger.info("Retrieved the case type for a " + solutionName);
        List<Attribute> attributes = new ArrayList<>();
        DeployedSolution solution = DeployedSolution.fetchInstance(objectStoreReference, solutionName);
        for (CaseType caseType : solution.getCaseTypes()) {
            if (types.contains(caseType.getName()))
                attributes.add(new Attribute(caseType.getDisplayName(), caseType.getName()));
        }
        return attributes;
    }

    public CaseType getCaseType(String id) {
        CaseType caseType = null;
        try {
            caseType = CaseType.fetchInstance(objectStoreReference, id);
            logger.info("Case type " + caseType.getDisplayName() + " with the id " + id);
        } catch (com.ibm.casemgmt.api.exception.CaseMgmtException e) {
            logger.warning("Case type for the id not found " + id);
        }
        return caseType;
    }

    public Case getCase(String caseID, List<String> propertiesFilter) {
        Case caseInstance = Case.fetchInstance(objectStoreReference, new Id(caseID), PropertiesTool.getPropertyFilter(propertiesFilter, true), null);
        logger.info("Retrieved the cases " + caseInstance.getIdentifier());

        return caseInstance;
    }

    public HashMap<String, String> GetPropertiesForCaseType(CaseType caseType, boolean customProp) {
        logger.info(" ------- ");
        String prefix = "---";
        if (customProp) prefix = caseType.getName().substring(0, caseType.getName().indexOf("_"));
        ClassDefinition caseTypeClass = Factory.ClassDefinition.fetchInstance(serverConfig.getOs(), caseType.getName(), null);
        HashMap<String, String> properties = new HashMap<>();
        for (int i = 0; i < caseTypeClass.get_PropertyDefinitions().size(); i++) {
            PropertyDefinition propertyDefinition = (PropertyDefinition) caseTypeClass.get_PropertyDefinitions().get(i);
            logger.info(propertyDefinition.get_DisplayName() + " ---  " + propertyDefinition.get_SymbolicName());
            if (customProp) {
                if (propertyDefinition.get_SymbolicName().startsWith(prefix))
                    properties.put(propertyDefinition.get_DisplayName(), propertyDefinition.get_SymbolicName());
            } else {
                properties.put(propertyDefinition.get_DisplayName(), propertyDefinition.get_SymbolicName());
            }
        }
        return properties;
    }

    public HashMap<String, String> getProperties(Case caseInstance, String dateFormat, boolean displayName) {
        logger.info("Retrieve properties for case " + caseInstance.getIdentifier());
        HashMap<String, String> properties = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        List<CaseMgmtProperty> listCase = caseInstance.getProperties().asList();
        for (CaseMgmtProperty caseProperty : listCase) {
            String value = "";
            if (caseProperty.getPropertyType().equals(TypeID.DATE) && caseProperty.getOriginalValue() != null)
                value = format.format(caseProperty.getOriginalValue());
            if (!caseProperty.getPropertyType().equals(TypeID.DATE) && caseProperty.getOriginalValue() != null)
                value = caseProperty.getOriginalValue().toString();
            if (displayName) properties.put(caseProperty.getDisplayName(), value);
            else properties.put(caseProperty.getSymbolicName(), value);
        }
        logger.info("Retrieved " + properties.size() + " properties for case " + caseInstance.getIdentifier());
        return properties;
    }


}

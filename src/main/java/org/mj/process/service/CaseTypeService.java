package org.mj.process.service;

import com.filenet.api.constants.TypeID;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
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

    private static Logger logger = Logger.getLogger(ServerECMTools.class.getName());
    ServerConfig serverConfig;
    ObjectStoreReference objectStoreReference;

    public CaseTypeService(ServerConfig config) {
        serverConfig = config;
        objectStoreReference = new ObjectStoreReference(serverConfig.getOs());
        SimpleVWSessionCache vwSessCache = new SimpleVWSessionCache();
        CaseMgmtContext cmc = new CaseMgmtContext(vwSessCache, new SimpleP8ConnectionCache());
        CaseMgmtContext.set(cmc);
    }

    public List<Attribute> getCaseTypeAttribute(Set<String> caseTypes) {
        List<Attribute> attributes = new ArrayList<>();
        List<DeployedSolution> solutions = getSolutions();
        for (String caseType : caseTypes) {
            for (DeployedSolution solution : solutions) {
                for (CaseType caseT : solution.getCaseTypes()) {
                    logger.info("compare : " + caseT.getId().toString() + " == " + caseType);
                    if (caseT.getId().toString().equals(caseType))
                        attributes.add(new Attribute(caseT.getDisplayName(), caseType));
                }
            }
        }
        logger.info("The number of caseAttribute " + attributes.size());
        return attributes;
    }


    public List<DeployedSolution> getSolutions() {
        String[] objectsStore = new String[1];
        objectsStore[0] = serverConfig.getOs().get_SymbolicName();
        ObjectStoreReference designOS = new ObjectStoreReference(serverConfig.getOs());
        List<DeployedSolution> solutions = DeployedSolution.fetchSolutions(serverConfig.getConnectionTool().getConnection().getURI(), objectsStore);
        logger.info("Retrieved " + solutions.size() + " Solutions");
        return solutions;
    }

    public Case getCase(String caseID, List<String> propertiesFilter) {
        Case caseInstance = Case.fetchInstance(objectStoreReference, new Id(caseID), PropertiesTool.getPropertyFilter(propertiesFilter, true), null);
        logger.info("Retrieved the case " + caseInstance.getIdentifier());
        return caseInstance;
    }

    public HashMap<String, String> GetPropertiesForCaseType(CaseType caseType) {
        logger.info("List property for Case Type " + caseType.getName());
        HashMap<String, String> properties = new HashMap<>();
        Properties folderProperties = caseType.getFolderReference().fetchCEObject().getProperties();
        for (Property property : folderProperties.toArray()) {
            logger.info("Property " + property.getPropertyName() + "" + property.getState());
            properties.put(property.getPropertyName(), property.getPropertyName());
        }
        return properties;
    }

    public CaseType getCaseTypeByID(String id) {
        List<DeployedSolution> solutions = getSolutions();
        for (DeployedSolution solution : solutions) {
            List<CaseType> caseTypes = solution.getCaseTypes();
            for (CaseType caseType : caseTypes) {
                logger.info("The case type " + caseType.getName());
                if (caseType.getId().toString().equals(id)) return caseType;
            }
        }
        return null;
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
